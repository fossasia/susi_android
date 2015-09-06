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
![ChatMessageView](https://raw.githubusercontent.com/himanshu-soni/ChatMessageView/master/screenshot/screen1.png)

### Installation
add gradle dependency to your dependency list:

```
dependencies {
  compile 'me.himanshusoni.chatmessageview:ChatMessageView:1.0.0'
}
```

### Use
1. Include `ChatMessageView` in your xml of adapter view with content inside.

```
<me.himanshusoni.chatmessageview.ChatMessageView xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:backgroundColor="#88BABABA"
        app:backgroundColorPressed="#FFBABABA"
        app:cornerRadius="3dp" >

        <TextView
            android:id="@+id/text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Hello" />
            
            ...
            
    </me.himanshusoni.chatmessageview.ChatMessageView>
```


### Customization
Attributes:

```
  app:arrowGravity="start|end|center"
  app:arrowPosition="right|left|top|bottom"
  app:arrowMargin="3dp"
  app:contentPadding="10dp"
  app:backgroundColor="#88BABABA"
  app:backgroundColorPressed="#FFBABABA"
  app:cornerRadius="3dp"
  app:showArrow="true"
```

Description:


- `arrowGravity` controls relative position of arrow. possible values are `start`,`end` and `center`. default is `left`.
- `arrowPosition` controls poition of the arrow outside the box. possible values are `right`,`left`,`top` and `bottom`. default is `left`.
- `arrowMargin` controls margin of arrow. If `arrowPosition` is `left` or `right` it controls top and bottom margin. else it controls left and right margin.
- `contentPadding` adjusts padding of content within the box.
- `backgroundColor` sets background color of `ChatMessageView` in normal mode including arrow. 
- `backgroundColorPressed` sets background color of `ChatMessageView` in pressed mode including arrow. 
- `cornerRadius` sets corner radius of the box.
- `showArrow` shows / hides arrow from `ChatMessageView`.



==================
developed to make programming easy. 

by Himanshu Soni (himanshusoni.me@gmail.com)

