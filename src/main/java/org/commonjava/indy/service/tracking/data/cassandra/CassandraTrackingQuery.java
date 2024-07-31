/**
 * Copyright (C) 2022-2023 Red Hat, Inc. (https://github.com/Commonjava/indy-tracking-service)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.indy.service.tracking.data.cassandra;

import com.datastax.driver.core.*;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import org.commonjava.indy.service.tracking.exception.ContentException;
import org.commonjava.indy.service.tracking.exception.IndyLifecycleException;
import org.commonjava.indy.service.tracking.exception.IndyWorkflowException;
import org.commonjava.indy.service.tracking.model.TrackedContent;
import org.commonjava.indy.service.tracking.model.TrackedContentEntry;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.datastax.driver.core.ConsistencyLevel.QUORUM;
import static org.commonjava.indy.service.tracking.data.cassandra.CassandraFoloUtil.TABLE_FOLO;
import static org.commonjava.indy.service.tracking.data.cassandra.CassandraFoloUtil.TABLE_FOLO_LEGACY;
import static org.commonjava.indy.service.tracking.data.cassandra.DtxTrackingRecord.fromCassandraRow;

@ApplicationScoped
public class CassandraTrackingQuery
{
    private final static String DOWNLOADS = "DOWNLOAD";

    private final static String UPLOADS = "UPLOAD";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    CassandraClient client;

    @Inject
    CassandraConfiguration config;

    private Session session;

    private Mapper<DtxTrackingRecord> trackingMapper;

    private PreparedStatement getTrackingRecord;

    private PreparedStatement getTrackingKeys;

    private PreparedStatement getLegacyTrackingKeys;

    private PreparedStatement getTrackingRecordsByTrackingKey;

    private PreparedStatement getLegacyTrackingRecordsByTrackingKey;

    private PreparedStatement isTrackingRecordExist;

    private PreparedStatement deleteTrackingRecordsByTrackingKey;

    public CassandraTrackingQuery()
    {
    }

    public CassandraTrackingQuery( CassandraClient client, CassandraConfiguration config )
    {
        this.client = client;
        this.config = config;
        init();
    }

    @PostConstruct
    public void init()
    {

        String keySpace = config.getKeyspace();

        session = client.getSession( keySpace );
        logger.info( "Cassandra keyspace replicas configured: {}", config.getKeyspaceReplicas() );
        session.execute( SchemaUtils.getSchemaCreateKeyspace( keySpace, config.getKeyspaceReplicas() ) );
        session.execute( CassandraFoloUtil.getSchemaCreateTableFolo( keySpace ) );
        session.execute( CassandraFoloUtil.getSchemaCreateTableFoloLegacy( keySpace ) );

        MappingManager manager = new MappingManager( session );

        trackingMapper = manager.mapper( DtxTrackingRecord.class, keySpace );

        getTrackingRecord = session.prepare( "SELECT * FROM " + keySpace + "." + TABLE_FOLO
                                                             + " WHERE tracking_key=? AND store_key=? AND path=? AND store_effect=?;" );
        getTrackingRecord.setConsistencyLevel( QUORUM );

        getTrackingKeys = session.prepare( "SELECT distinct tracking_key FROM " + keySpace + "." + TABLE_FOLO + ";" );
        getTrackingKeys.setConsistencyLevel( QUORUM );

        getLegacyTrackingKeys = session.prepare(
                        "SELECT distinct tracking_key FROM " + keySpace + "." + TABLE_FOLO_LEGACY + ";" );

        getTrackingRecordsByTrackingKey =
                        session.prepare( "SELECT * FROM " + keySpace + "." + TABLE_FOLO + " WHERE tracking_key=?;" );
        getTrackingRecordsByTrackingKey.setConsistencyLevel( QUORUM );

        getLegacyTrackingRecordsByTrackingKey = session.prepare(
                        "SELECT * FROM " + keySpace + "." + TABLE_FOLO_LEGACY + " WHERE tracking_key=?;" );
        getLegacyTrackingRecordsByTrackingKey.setConsistencyLevel( QUORUM );

        isTrackingRecordExist = session.prepare(
                        "SELECT count(*) FROM " + keySpace + "." + TABLE_FOLO + " WHERE tracking_key=?;" );

        deleteTrackingRecordsByTrackingKey =
                        session.prepare( "DELETE FROM " + keySpace + "." + TABLE_FOLO + " WHERE tracking_key=?;" );
        deleteTrackingRecordsByTrackingKey.setConsistencyLevel( QUORUM );

        logger.info( "-- Cassandra Folo Records Keyspace and Tables created" );
    }

    public boolean recordArtifact( TrackedContentEntry entry ) throws ContentException, IndyWorkflowException
    {

        String buildId = entry.getTrackingKey().getId();
        String storeKey = entry.getStoreKey().toString();
        String path = entry.getPath();
        String effect = entry.getEffect().toString();

        BoundStatement bind = getTrackingRecord.bind( buildId, storeKey, path, effect );
        ResultSet trackingRecord = session.execute( bind );
        Row one = trackingRecord.one();

        if ( one != null )
        {
            DtxTrackingRecord dtxTrackingRecord = fromCassandraRow( one );
            Boolean state = dtxTrackingRecord.getState();
            if ( state )
            {
                throw new ContentException( "Tracking record: {} is already sealed!", entry.getTrackingKey() );
            }
        }
        // Always override prev one since some builds may upload artifact more than once
        DtxTrackingRecord dtxTrackingRecord = new DtxTrackingRecord( entry );
        trackingMapper.save( dtxTrackingRecord ); //  optional Options with TTL, timestamp...
        return true;
    }

    public void delete( TrackingKey key )
    {
        logger.info( "Delete tracking records, tracking_id: {}", key.getId() );
        BoundStatement bind = deleteTrackingRecordsByTrackingKey.bind( key.getId() );
        session.execute( bind );
    }

    public void replaceTrackingRecord( TrackedContent record )
    {
        saveTrackedContentRecords( record );
    }

    public boolean hasRecord( TrackingKey key )
    {
        BoundStatement bind = isTrackingRecordExist.bind( key );
        ResultSet result = session.execute( bind );
        Row row = result.one();
        boolean exists = false;
        if ( row != null )
        {
            long count = row.get( 0, Long.class );
            exists = count > 0;
        }
        logger.trace( "{} {}", key, ( exists ? "exists" : "not exists" ) );
        return exists;
    }

    public TrackedContent get( TrackingKey key )
    {
        List<DtxTrackingRecord> trackingRecords = getDtxTrackingRecordsFromDb( key );
        if ( trackingRecords == null || trackingRecords.isEmpty() )
        {
            return null;
        }
        return transformDtxTrackingRecordToTrackingContent( key, trackingRecords );
    }

    public TrackedContent seal( TrackingKey trackingKey )
    {
        List<DtxTrackingRecord> trackingRecords = getDtxTrackingRecordsFromDb( trackingKey );

        if ( trackingRecords == null || trackingRecords.isEmpty() )
        {
            logger.debug( "Tracking record: {} doesn't exist! Returning empty record.", trackingKey );
            return new TrackedContent( trackingKey, new HashSet<>(), new HashSet<>() );
        }

        DtxTrackingRecord recordCheck = trackingRecords.get( 0 );
        if ( recordCheck.getState() )
        {
            logger.debug( "Tracking record: {} already sealed! Returning sealed record.", trackingKey );
            return transformDtxTrackingRecordToTrackingContent( trackingKey, trackingRecords );
        }
        logger.debug( "Sealing record for: {}", trackingKey );
        for ( DtxTrackingRecord record : trackingRecords )
        {
            record.setState( true );
            trackingMapper.save( record );
        }
        return transformDtxTrackingRecordToTrackingContent( trackingKey, trackingRecords );
    }

    public Set<TrackingKey> getInProgressTrackingKey()
    {
        throw new UnsupportedOperationException(
                        "Getting in-progress tracking keys are not supported by Cassandra Folo" );
    }

    public Set<TrackingKey> getSealedTrackingKey()
    {
        return getTrackingKeys();
    }

    // This may fail given a huge dataset (oom). Only used for test purpose !
    public Set<TrackedContent> getSealed()
    {

        Set<TrackedContent> trackedContents = new HashSet<>();
        Set<TrackingKey> sealedTrackingKeys = getSealedTrackingKey();

        for ( TrackingKey trackingKey : sealedTrackingKeys )
        {

            List<DtxTrackingRecord> dtxTrackingRecordsFromDb = getDtxTrackingRecordsFromDb( trackingKey );
            TrackedContent trackedContent =
                            transformDtxTrackingRecordToTrackingContent( trackingKey, dtxTrackingRecordsFromDb );

            trackedContents.add( trackedContent );
        }

        return trackedContents;
    }

    public void addSealedRecord( TrackedContent record )
    {
        saveTrackedContentRecords( record );
    }

    public void start() throws IndyLifecycleException
    {
        logger.info( "--- FoloRecordsCassandra starting up" );
    }

    public int getStartupPriority()
    {
        return 0;
    }

    public String getId()
    {
        return "Folo2Cassandra";
    }

    private TrackedContent transformDtxTrackingRecordToTrackingContent( TrackingKey trackingKey,
                                                                        List<DtxTrackingRecord> trackingRecords )
    {

        List<TrackedContentEntry> records = new ArrayList<>();

        for ( DtxTrackingRecord record : trackingRecords )
        {
            records.add( DtxTrackingRecord.toTrackingContentEntry( record ) );
        }
        Set<TrackedContentEntry> uploads = records.stream()
                                                  .filter( record -> record.getEffect().toString().equals( UPLOADS ) )
                                                  .collect( Collectors.toSet() );

        //        logger.warn("-- Processing {} uploads  from  tracking key {} " ,  uploads.size() ,  trackingKey);

        Set<TrackedContentEntry> downloads = records.stream()
                                                    .filter( record -> record.getEffect()
                                                                             .toString()
                                                                             .equals( DOWNLOADS ) )
                                                    .collect( Collectors.toSet() );

        //        logger.warn("-- Processing {} downloads  from  tracking key {} " ,  downloads.size() ,  trackingKey);

        TrackedContent trackedContent = new TrackedContent( trackingKey, uploads, downloads );

        return trackedContent;

    }

    private List<DtxTrackingRecord> getLegacyDtxTrackingRecordsFromDb( TrackingKey trackingKey )
    {
        BoundStatement bind = getLegacyTrackingRecordsByTrackingKey.bind( trackingKey.getId() );
        ResultSet execute = session.execute( bind );
        List<Row> rows = execute.all();
        return fetchRecordsFromRows( rows );
    }

    private List<DtxTrackingRecord> getDtxTrackingRecordsFromDb( TrackingKey trackingKey )
    {
        BoundStatement bind = getTrackingRecordsByTrackingKey.bind( trackingKey.getId() );
        ResultSet execute = session.execute( bind );
        List<Row> rows = execute.all();
        return fetchRecordsFromRows( rows );
    }

    private List<DtxTrackingRecord> fetchRecordsFromRows( List<Row> rows )
    {
        List<DtxTrackingRecord> trackingRecords = new ArrayList<>();
        Iterator<Row> iteratorDtxTrackingRecords = rows.iterator();
        while ( iteratorDtxTrackingRecords.hasNext() )
        {
            Row next = iteratorDtxTrackingRecords.next();
            DtxTrackingRecord dtxTrackingRecord = new DtxTrackingRecord();
            dtxTrackingRecord.setTrackingKey( next.getString( "tracking_key" ) );
            dtxTrackingRecord.setState( next.getBool( "sealed" ) );
            dtxTrackingRecord.setLocalUrl( next.getString( "local_url" ) );
            dtxTrackingRecord.setOriginUrl( next.getString( "origin_url" ) );
            dtxTrackingRecord.setTimestamps( next.getSet( "timestamps", Long.class ) );
            dtxTrackingRecord.setPath( next.getString( "path" ) );
            dtxTrackingRecord.setStoreEffect( next.getString( "store_effect" ) );
            dtxTrackingRecord.setSha256( next.getString( "sha256" ) );
            dtxTrackingRecord.setSha1( next.getString( "sha1" ) );
            dtxTrackingRecord.setMd5( next.getString( "md5" ) );
            dtxTrackingRecord.setSize( next.getLong( "size" ) );
            dtxTrackingRecord.setStoreKey( next.getString( "store_key" ) );
            dtxTrackingRecord.setAccessChannel( next.getString( "access_channel" ) );
            trackingRecords.add( dtxTrackingRecord );
        }
        return trackingRecords;
    }

    private void saveTrackedContentRecords( TrackedContent record )
    {
        Set<TrackedContentEntry> downloads = record.getDownloads();
        Set<TrackedContentEntry> uploads = record.getUploads();
        TrackingKey key = record.getKey();

        for ( TrackedContentEntry downloadEntry : downloads )
        {
            DtxTrackingRecord downloadRecord = DtxTrackingRecord.fromTrackedContentEntry( downloadEntry, true );
            trackingMapper.save( downloadRecord );
        }

        for ( TrackedContentEntry uploadEntry : uploads )
        {
            DtxTrackingRecord uploadRecord = DtxTrackingRecord.fromTrackedContentEntry( uploadEntry, true );
            trackingMapper.save( uploadRecord );
        }
    }

    public TrackedContent getLegacy( TrackingKey key )
    {
        List<DtxTrackingRecord> trackingRecords = getLegacyDtxTrackingRecordsFromDb( key );
        if ( trackingRecords == null || trackingRecords.isEmpty() )
        {
            return null;
        }
        return transformDtxTrackingRecordToTrackingContent( key, trackingRecords );
    }

    public Set<TrackingKey> getLegacyTrackingKeys()
    {
        BoundStatement statement = getLegacyTrackingKeys.bind();
        return getTrackingKeys( statement );
    }

    private Set<TrackingKey> getTrackingKeys()
    {
        BoundStatement statement = getTrackingKeys.bind();
        return getTrackingKeys( statement );
    }

    private Set<TrackingKey> getTrackingKeys( BoundStatement statement )
    {
        ResultSet resultSet = session.execute( statement );
        List<Row> all = resultSet.all();
        Iterator<Row> iterator = all.iterator();

        Set<TrackingKey> trackingKeys = new HashSet<>();
        while ( iterator.hasNext() )
        {
            Row next = iterator.next();
            String tracking_key = next.getString( "tracking_key" );
            trackingKeys.add( new TrackingKey( tracking_key ) );
        }
        return trackingKeys.stream().collect( Collectors.toSet() );
    }

    public void createDtxTrackingRecord( DtxTrackingRecord trackingRecord )
    {
        trackingMapper.save( trackingRecord );
    }

}
