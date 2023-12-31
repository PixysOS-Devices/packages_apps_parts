/**
 * Copyright (C) 2020 DerpFest ROM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.device.fpsinfo;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Handler;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import org.lineageos.settings.device.Constants;

// TODO: Add FPS drawables
public class FPSTileService extends TileService {

  private boolean isShowing = false;

  public FPSTileService() { }

  @Override
  public void onStartListening() {
      ActivityManager manager =
              (ActivityManager) getSystemService(this.ACTIVITY_SERVICE);
      for (ActivityManager.RunningServiceInfo service :
              manager.getRunningServices(Integer.MAX_VALUE)) {
          if (FPSInfoService.class.getName().equals(
                  service.service.getClassName())) {
              isShowing = true;
          }
      }
      updateTile();
      super.onStartListening();
  }

  @Override
  public void onClick() {
      Intent fpsinfo = new Intent(this, FPSInfoService.class);
      if (!isShowing)
          this.startService(fpsinfo);
      else
          this.stopService(fpsinfo);
      isShowing = !isShowing;
      updateTile();
      super.onClick();
  }

  private void updateTile() {
      final Tile tile = getQsTile();
      tile.setState(isShowing ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
      tile.updateTile();
  }

}
