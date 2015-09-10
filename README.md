[![Android Gems](http://www.android-gems.com/badge/himanshu-soni/ChatMessageView.svg?branch=master)](http://www.android-gems.com/lib/himanshu-soni/ChatMessageView)

# ChatMessageView
ChatMessageView helps you to create chat message view quickly like a typical chatting application.
Its a container view, so you can add any type of message such as TextView or any customize TextView, ImageView, etc.


## Features
1. Can have any child inside of it.
2. You can change color of `ChatMessageView` normal and pressed.
3. Adjustable arrow position (top, bottom, left, right)
4. Adjustable arrow gravity (start, end, center)
5. Chat view without arrow

### Sample Screen
![ChatMessageView](https://raw.githubusercontent.com/himanshu-soni/ChatMessageView/master/screenshot/screen2.jpg)

### Installation
add gradle dependency to your dependency list:

``` groovy
dependencies {
	compile 'me.himanshusoni.chatmessageview:chat-message-view:1.0.2'
}
```

### Use
1. Include `ChatMessageView` in your xml of adapter view with content inside.

``` xml
<me.himanshusoni.chatmessageview.ChatMessageView
	xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cmv_backgroundColor="#88BABABA"
    app:cmv_backgroundColorPressed="#FFBABABA"
    app:cmv_cornerRadius="3dp" >

    <TextView
        android:id="@+id/text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello" />

        <!-- ... -->

</me.himanshusoni.chatmessageview.ChatMessageView>
```


### Customization
Attributes:

``` xml
app:cmv_arrowGravity="start|end|center"
app:cmv_arrowPosition="right|left|top|bottom"
app:cmv_arrowMargin="3dp"
app:cmv_contentPadding="10dp"
app:cmv_backgroundColor="#88BABABA"
app:cmv_backgroundColorPressed="#FFBABABA"
app:cmv_cornerRadius="3dp"
app:cmv_showArrow="true|false"
```

Description:


- `cmv_arrowGravity` controls relative position of arrow. possible values are `start`,`end` and `center`. default is `left`.
- `cmv_arrowPosition` controls poition of the arrow outside the box. possible values are `right`,`left`,`top` and `bottom`. default is `left`.
- `cmv_arrowMargin` controls margin of arrow. If `cmv_arrowPosition` is `left` or `right` it controls top and bottom margin. else it controls left and right margin.
- `cmv_contentPadding` adjusts padding of content within the box.
- `cmv_backgroundColor` sets background color of `ChatMessageView` in normal mode including arrow.
- `cmv_backgroundColorPressed` sets background color of `ChatMessageView` in pressed mode including arrow.
- `cmv_cornerRadius` sets corner radius of the box.
- `cmv_showArrow` shows / hides arrow from `ChatMessageView`.



==================
developed to make programming easy.

by Himanshu Soni (himanshusoni.me@gmail.com)

