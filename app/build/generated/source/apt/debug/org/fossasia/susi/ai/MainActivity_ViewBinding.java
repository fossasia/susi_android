// Generated code from Butter Knife. Do not modify!
package org.fossasia.susi.ai;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding<T extends MainActivity> implements Unbinder {
  protected T target;

  private View view2131492976;

  private View view2131492978;

  @UiThread
  public MainActivity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.coordinatorLayout = Utils.findRequiredViewAsType(source, R.id.coordinator_layout, "field 'coordinatorLayout'", CoordinatorLayout.class);
    target.rvChatFeed = Utils.findRequiredViewAsType(source, R.id.rv_chat_feed, "field 'rvChatFeed'", RecyclerView.class);
    view = Utils.findRequiredView(source, R.id.iv_image, "field 'ivImage' and method 'onClick'");
    target.ivImage = Utils.castView(view, R.id.iv_image, "field 'ivImage'", ImageView.class);
    view2131492976 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    target.etMessage = Utils.findRequiredViewAsType(source, R.id.et_message, "field 'etMessage'", EditText.class);
    view = Utils.findRequiredView(source, R.id.btn_send, "field 'btnSend' and method 'onClick'");
    target.btnSend = Utils.castView(view, R.id.btn_send, "field 'btnSend'", FloatingActionButton.class);
    view2131492978 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    target.sendMessageLayout = Utils.findRequiredViewAsType(source, R.id.send_message_layout, "field 'sendMessageLayout'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.coordinatorLayout = null;
    target.rvChatFeed = null;
    target.ivImage = null;
    target.etMessage = null;
    target.btnSend = null;
    target.sendMessageLayout = null;

    view2131492976.setOnClickListener(null);
    view2131492976 = null;
    view2131492978.setOnClickListener(null);
    view2131492978 = null;

    this.target = null;
  }
}
