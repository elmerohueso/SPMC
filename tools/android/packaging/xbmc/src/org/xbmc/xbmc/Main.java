package com.semperpax.spmc;

import android.app.NativeActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Main extends NativeActivity 
{
  native void _onNewIntent(Intent intent);

  public Main() 
  {
    super();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onNewIntent(Intent intent)
  {
    super.onNewIntent(intent);
    try {
      _onNewIntent(intent);
    } catch (UnsatisfiedLinkError e) {
      Log.e("Main", "Native not registered");
    }
  }

  @Override
  public void onResume()
  {
    super.onResume();

    if (android.os.Build.VERSION.SDK_INT >= 19) {
      // Immersive mode

      // Constants from API > 14
      final int API_SYSTEM_UI_FLAG_LAYOUT_STABLE = 0x00000100;
      final int API_SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION = 0x00000200;
      final int API_SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN = 0x00000400;
      final int API_SYSTEM_UI_FLAG_FULLSCREEN = 0x00000004;
      final int API_SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;

      View thisView = getWindow().getDecorView();
      thisView.setSystemUiVisibility(
                API_SYSTEM_UI_FLAG_LAYOUT_STABLE
              | API_SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
              | API_SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              | API_SYSTEM_UI_FLAG_FULLSCREEN
              | API_SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
  }

  @Override
  public void onDestroy()
  {
    new Thread() {
      public void run()
      {
        try
        {
          sleep(1000);
          android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {}
      }
    }.start();
    super.onDestroy();
  }
}
