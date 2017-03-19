package comqq.example.asus_pc.baidudemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.lbsapi.BMapManager;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by asus-pc on 2017/3/19.
 */

public class PanoramaDemoActivityMain extends Activity {
   private PanoramaView panoramaView;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initBMapManager();
        setContentView(R.layout.panorama);
        panoramaView= (PanoramaView) findViewById(R.id.panorama);
        Intent intent=getIntent();
        LatLng latLng= (LatLng) intent.getExtras().get("postion");
        panoramaView.setPanorama(latLng.longitude,latLng.latitude);
    }
    private void initBMapManager() {
        DemoApplication app = (DemoApplication) this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(app);
            app.mBMapManager.init(new DemoApplication.MyGeneralListener());
        }
    }
    protected void onPause() {
        super.onPause();
        panoramaView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        panoramaView.onResume();
    }

    @Override
    protected void onDestroy() {
        panoramaView.destroy();
        super.onDestroy();
    }
}
