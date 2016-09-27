// Generated code from Butter Knife. Do not modify!
package org.fossasia.susi.ai.adapters.viewHolders;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import org.fossasia.susi.ai.R;

public class ChatViewHolder_ViewBinding<T extends ChatViewHolder> implements Unbinder {
  protected T target;

  @UiThread
  public ChatViewHolder_ViewBinding(T target, View source) {
    this.target = target;

    target.chatTextView = Utils.findRequiredViewAsType(source, R.id.text, "field 'chatTextView'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.chatTextView = null;

    this.target = null;
  }
}
