package player;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

public class PlayerFactory {
    private Context context;

    public PlayerFactory(Context context) {
        this.context = context;
    }

    public Player playerForUrl(String s) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder()
                                                            .setInitialBitrateEstimate(86000)
                                                            .build();
        SimpleExoPlayer exoplayer = createExoplayer(bandwidthMeter);
        MediaSource dashDataSource = createDashDataSource(s,bandwidthMeter);

        Player player = new Player(exoplayer);

        exoplayer.prepare(dashDataSource);

        return player;
    }

    private MediaSource createDashDataSource(String s, DefaultBandwidthMeter defaultBandwidthMeter) {
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("integrationtests",defaultBandwidthMeter);
        DefaultDashChunkSource.Factory chunkSourceFactory = new DefaultDashChunkSource.Factory(dataSourceFactory);
        Uri uri = Uri.parse(s);
        DashMediaSource.Factory factory = new DashMediaSource.Factory(chunkSourceFactory, dataSourceFactory);
        return factory.createMediaSource(uri);
    }

    private SimpleExoPlayer createExoplayer(DefaultBandwidthMeter defaultBandwidthMeter) {

        BandwidthMeter bandwidthMeter = defaultBandwidthMeter;
        AdaptiveTrackSelection.Factory factory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector(factory);

        return ExoPlayerFactory.newSimpleInstance(context,defaultTrackSelector);
    }
}
