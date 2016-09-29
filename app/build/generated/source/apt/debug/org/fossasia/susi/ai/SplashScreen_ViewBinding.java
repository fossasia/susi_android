// Generated code from Butter Knife. Do not modify!
package org.fossasia.susi.ai;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SplashScreen_ViewBinding<T extends SplashScreen> implements Unbinder {
  protected T target;

  @UiThread
  public SplashScreen_ViewBinding(T target, View source) {
    this.target = target;

    target.imageView = Utils.findRequiredViewAsType(source, R.id.splash_image, "field 'imageView'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.imageView = null;

    this.target = null;
  }
}
