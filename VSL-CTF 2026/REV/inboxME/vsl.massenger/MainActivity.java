package com.vsl.massenger;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.textfield.TextInputEditText;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010%\u001a\u00020&2\b\u0010'\u001a\u0004\u0018\u00010(H\u0014J\b\u0010)\u001a\u00020&H\u0014R\u0018\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u001b\u0010\f\u001a\u00020\r8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0010\u0010\u0011\u001a\u0004\b\u000e\u0010\u000fR\u001b\u0010\u0012\u001a\u00020\u00138BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0016\u0010\u0011\u001a\u0004\b\u0014\u0010\u0015R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0019\u001a\u00060\u001aj\u0002`\u001bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u001b\u0010\u001d\u001a\u00020\r8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u001f\u0010\u0011\u001a\u0004\b\u001e\u0010\u000fR\u001b\u0010 \u001a\u00020!8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b$\u0010\u0011\u001a\u0004\b\"\u0010#¨\u0006*"}, d2 = {"Lcom/vsl/massenger/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "executor", "Ljava/util/concurrent/ExecutorService;", "kotlin.jvm.PlatformType", "Ljava/util/concurrent/ExecutorService;", "uiHandler", "Landroid/os/Handler;", "client", "Lcom/vsl/massenger/TcpClient;", "pinnedSha256", "", "getPinnedSha256", "()Ljava/lang/String;", "pinnedSha256$delegate", "Lkotlin/Lazy;", "pinnedCert", "", "getPinnedCert", "()[B", "pinnedCert$delegate", "isLoggedIn", "", "chatBuffer", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "currentUsername", "fixedHost", "getFixedHost", "fixedHost$delegate", "fixedPort", "", "getFixedPort", "()I", "fixedPort$delegate", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "app_release"}, k = 1, mv = {2, 0, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class MainActivity extends AppCompatActivity {
    private TcpClient client;
    private boolean isLoggedIn;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    /* renamed from: pinnedSha256$delegate, reason: from kotlin metadata */
    private final Lazy pinnedSha256 = LazyKt.lazy(new Function0() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda20
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return MainActivity.pinnedSha256_delegate$lambda$1(this.f$0);
        }
    });

    /* renamed from: pinnedCert$delegate, reason: from kotlin metadata */
    private final Lazy pinnedCert = LazyKt.lazy(new Function0() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda21
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return MainActivity.pinnedCert_delegate$lambda$3(this.f$0);
        }
    });
    private final StringBuilder chatBuffer = new StringBuilder();
    private String currentUsername = "";

    /* renamed from: fixedHost$delegate, reason: from kotlin metadata */
    private final Lazy fixedHost = LazyKt.lazy(new Function0() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda1
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return MainActivity.fixedHost_delegate$lambda$4();
        }
    });

    /* renamed from: fixedPort$delegate, reason: from kotlin metadata */
    private final Lazy fixedPort = LazyKt.lazy(new Function0() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda2
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return Integer.valueOf(MainActivity.fixedPort_delegate$lambda$5());
        }
    });

    private final String getPinnedSha256() {
        return (String) this.pinnedSha256.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String pinnedSha256_delegate$lambda$1(MainActivity mainActivity) throws Resources.NotFoundException, IOException {
        InputStream inputStreamOpenRawResource = mainActivity.getResources().openRawResource(R.raw.pin);
        Intrinsics.checkNotNullExpressionValue(inputStreamOpenRawResource, "openRawResource(...)");
        Reader inputStreamReader = new InputStreamReader(inputStreamOpenRawResource, Charsets.UTF_8);
        BufferedReader bufferedReader = inputStreamReader instanceof BufferedReader ? (BufferedReader) inputStreamReader : new BufferedReader(inputStreamReader, 8192);
        try {
            String string = StringsKt.trim((CharSequence) TextStreamsKt.readText(bufferedReader)).toString();
            CloseableKt.closeFinally(bufferedReader, null);
            return string;
        } finally {
        }
    }

    private final byte[] getPinnedCert() {
        return (byte[]) this.pinnedCert.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final byte[] pinnedCert_delegate$lambda$3(MainActivity mainActivity) throws Resources.NotFoundException, IOException {
        InputStream inputStreamOpenRawResource = mainActivity.getResources().openRawResource(R.raw.server);
        try {
            InputStream inputStream = inputStreamOpenRawResource;
            Intrinsics.checkNotNull(inputStream);
            byte[] bytes = ByteStreamsKt.readBytes(inputStream);
            CloseableKt.closeFinally(inputStreamOpenRawResource, null);
            return bytes;
        } finally {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String fixedHost_delegate$lambda$4() {
        return NativeProtocol.INSTANCE.getHostFromNative();
    }

    private final String getFixedHost() {
        return (String) this.fixedHost.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final int fixedPort_delegate$lambda$5() {
        return NativeProtocol.INSTANCE.getPortFromNative();
    }

    private final int getFixedPort() {
        return ((Number) this.fixedPort.getValue()).intValue();
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.registerButton);
        Button button2 = (Button) findViewById(R.id.loginButton);
        final TextInputEditText textInputEditText = (TextInputEditText) findViewById(R.id.usernameInput);
        final TextInputEditText textInputEditText2 = (TextInputEditText) findViewById(R.id.passwordInput);
        final TextInputEditText textInputEditText3 = (TextInputEditText) findViewById(R.id.messageInput);
        Button button3 = (Button) findViewById(R.id.sendButton);
        final CardView cardView = (CardView) findViewById(R.id.loginCard);
        final View viewFindViewById = findViewById(R.id.chatContainer);
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.chatMessagesContainer);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.chatScrollView);
        final Button button4 = (Button) findViewById(R.id.menuButton);
        this.executor.execute(new Runnable() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.onCreate$lambda$16(this.f$0, cardView, viewFindViewById, button4, linearLayout, scrollView);
            }
        });
        button.setOnClickListener(new View.OnClickListener() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda14
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.onCreate$lambda$18(textInputEditText, textInputEditText2, this, view);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda15
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.onCreate$lambda$20(textInputEditText, textInputEditText2, this, view);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda16
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.onCreate$lambda$25(this.f$0, view);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda17
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.onCreate$lambda$28(textInputEditText3, this, linearLayout, scrollView, view);
            }
        });
    }

    private static final void onCreate$showToast(final MainActivity mainActivity, final String str) {
        mainActivity.uiHandler.post(new Runnable() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.onCreate$showToast$lambda$6(this.f$0, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$showToast$lambda$6(MainActivity mainActivity, String str) {
        Toast.makeText(mainActivity, str, 0).show();
    }

    private static final int onCreate$dpToPx(MainActivity mainActivity, int i) {
        return (int) (i * mainActivity.getResources().getDisplayMetrics().density);
    }

    static /* synthetic */ void onCreate$addChatMessage$default(MainActivity mainActivity, LinearLayout linearLayout, ScrollView scrollView, String str, boolean z, boolean z2, int i, Object obj) {
        if ((i & 16) != 0) {
            z = false;
        }
        if ((i & 32) != 0) {
            z2 = false;
        }
        onCreate$addChatMessage(mainActivity, linearLayout, scrollView, str, z, z2);
    }

    private static final void onCreate$addChatMessage(final MainActivity mainActivity, final LinearLayout linearLayout, final ScrollView scrollView, final String str, final boolean z, final boolean z2) {
        mainActivity.uiHandler.post(new Runnable() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.onCreate$addChatMessage$lambda$13(this.f$0, linearLayout, scrollView, z2, z, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:17:0x00af  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final void onCreate$addChatMessage$lambda$13(com.vsl.massenger.MainActivity r14, android.widget.LinearLayout r15, final android.widget.ScrollView r16, boolean r17, boolean r18, java.lang.String r19) {
        /*
            Method dump skipped, instructions count: 329
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.vsl.massenger.MainActivity.onCreate$addChatMessage$lambda$13(com.vsl.massenger.MainActivity, android.widget.LinearLayout, android.widget.ScrollView, boolean, boolean, java.lang.String):void");
    }

    private static final void onCreate$updateUI(final MainActivity mainActivity, final CardView cardView, final View view, final Button button) {
        mainActivity.uiHandler.post(new Runnable() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.onCreate$updateUI$lambda$14(this.f$0, cardView, view, button);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$updateUI$lambda$14(MainActivity mainActivity, CardView cardView, View view, Button button) {
        if (mainActivity.isLoggedIn) {
            cardView.setVisibility(8);
            view.setVisibility(0);
            button.setVisibility(0);
        } else {
            cardView.setVisibility(0);
            view.setVisibility(8);
            button.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$16(final MainActivity mainActivity, final CardView cardView, final View view, final Button button, final LinearLayout linearLayout, final ScrollView scrollView) {
        try {
            if (FridaGuard.INSTANCE.detected()) {
                onCreate$showToast(mainActivity, "⚠️ Security check failed");
                return;
            }
            TcpClient tcpClient = mainActivity.client;
            if (tcpClient != null) {
                tcpClient.close();
            }
            TcpClient tcpClient2 = new TcpClient(mainActivity.getFixedHost(), mainActivity.getFixedPort(), mainActivity.getPinnedSha256(), mainActivity.getPinnedCert());
            tcpClient2.connect(new Function1() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda13
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return MainActivity.onCreate$lambda$16$lambda$15(this.f$0, cardView, view, button, linearLayout, scrollView, (String) obj);
                }
            });
            mainActivity.client = tcpClient2;
            onCreate$showToast(mainActivity, "✅ Connected to server");
        } catch (Exception e) {
            onCreate$showToast(mainActivity, "❌ Connection failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit onCreate$lambda$16$lambda$15(MainActivity mainActivity, CardView cardView, View view, Button button, LinearLayout linearLayout, ScrollView scrollView, String msg) {
        Intrinsics.checkNotNullParameter(msg, "msg");
        String str = msg;
        if (StringsKt.contains((CharSequence) str, (CharSequence) "Login OK", true) || StringsKt.contains((CharSequence) str, (CharSequence) "Login successful", true) || StringsKt.contains((CharSequence) str, (CharSequence) "Welcome", true)) {
            mainActivity.isLoggedIn = true;
            onCreate$updateUI(mainActivity, cardView, view, button);
            onCreate$addChatMessage$default(mainActivity, linearLayout, scrollView, "✅ " + msg, false, true, 16, null);
            onCreate$showToast(mainActivity, "🎉 Login successful!");
        } else if (StringsKt.contains((CharSequence) str, (CharSequence) "Login failed", true) || StringsKt.contains((CharSequence) str, (CharSequence) "Login FAIL", true) || StringsKt.contains((CharSequence) str, (CharSequence) "Invalid credentials", true) || StringsKt.contains((CharSequence) str, (CharSequence) "Authentication failed", true)) {
            mainActivity.isLoggedIn = false;
            onCreate$updateUI(mainActivity, cardView, view, button);
            onCreate$showToast(mainActivity, "❌ " + msg);
        } else if (StringsKt.contains((CharSequence) str, (CharSequence) "Logout", true) || StringsKt.contains((CharSequence) str, (CharSequence) "Logged out", true)) {
            mainActivity.isLoggedIn = false;
            onCreate$updateUI(mainActivity, cardView, view, button);
            onCreate$addChatMessage$default(mainActivity, linearLayout, scrollView, "👋 " + msg, false, true, 16, null);
            onCreate$showToast(mainActivity, "Logged out");
        } else if (StringsKt.contains((CharSequence) str, (CharSequence) "Online", true)) {
            onCreate$addChatMessage$default(mainActivity, linearLayout, scrollView, "👥 " + msg, false, true, 16, null);
        } else if (StringsKt.contains((CharSequence) str, (CharSequence) "Pong", true) || StringsKt.contains((CharSequence) str, (CharSequence) "PONG", true)) {
            onCreate$addChatMessage$default(mainActivity, linearLayout, scrollView, "📡 PONG", false, true, 16, null);
        } else if (StringsKt.startsWith$default(msg, "New message from ", false, 2, (Object) null)) {
            List listSplit$default = StringsKt.split$default((CharSequence) StringsKt.substringAfter$default(msg, "New message from ", (String) null, 2, (Object) null), new String[]{" -> "}, false, 2, 2, (Object) null);
            if (listSplit$default.size() == 2) {
                String str2 = (String) listSplit$default.get(0);
                String str3 = (String) listSplit$default.get(1);
                if (Intrinsics.areEqual(str2, mainActivity.currentUsername)) {
                    onCreate$addChatMessage$default(mainActivity, linearLayout, scrollView, str3, true, false, 32, null);
                } else {
                    onCreate$addChatMessage$default(mainActivity, linearLayout, scrollView, str2 + "\n" + str3, false, false, 32, null);
                }
            } else {
                onCreate$addChatMessage$default(mainActivity, linearLayout, scrollView, msg, false, false, 32, null);
            }
        } else if (StringsKt.startsWith$default(msg, "History: ", false, 2, (Object) null)) {
            String strSubstringAfter$default = StringsKt.substringAfter$default(msg, "History: ", (String) null, 2, (Object) null);
            String str4 = strSubstringAfter$default;
            if (str4.length() > 0 && !StringsKt.contains$default((CharSequence) str4, (CharSequence) "Saved:", false, 2, (Object) null)) {
                if (StringsKt.contains$default((CharSequence) str4, (CharSequence) " -> ", false, 2, (Object) null)) {
                    List listSplit$default2 = StringsKt.split$default((CharSequence) str4, new String[]{" -> "}, false, 2, 2, (Object) null);
                    if (listSplit$default2.size() == 2) {
                        String str5 = (String) listSplit$default2.get(0);
                        String str6 = (String) listSplit$default2.get(1);
                        if (Intrinsics.areEqual(str5, mainActivity.currentUsername)) {
                            onCreate$addChatMessage$default(mainActivity, linearLayout, scrollView, str6, true, false, 32, null);
                        } else {
                            onCreate$addChatMessage$default(mainActivity, linearLayout, scrollView, str5 + "\n" + str6, false, false, 32, null);
                        }
                    } else {
                        onCreate$addChatMessage$default(mainActivity, linearLayout, scrollView, strSubstringAfter$default, false, false, 32, null);
                    }
                } else {
                    onCreate$addChatMessage$default(mainActivity, linearLayout, scrollView, strSubstringAfter$default, false, false, 32, null);
                }
            }
        } else if (StringsKt.contains((CharSequence) str, (CharSequence) "joined the room", true) || StringsKt.contains((CharSequence) str, (CharSequence) "left the room", true)) {
            onCreate$addChatMessage$default(mainActivity, linearLayout, scrollView, msg, false, true, 16, null);
        } else if (!StringsKt.startsWith$default(msg, "Saved:", false, 2, (Object) null)) {
            if (StringsKt.contains((CharSequence) str, (CharSequence) "Register", true)) {
                onCreate$addChatMessage$default(mainActivity, linearLayout, scrollView, msg, false, true, 16, null);
                onCreate$showToast(mainActivity, String.valueOf(msg));
            } else if (str.length() > 0) {
                onCreate$addChatMessage$default(mainActivity, linearLayout, scrollView, msg, false, true, 16, null);
            }
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$18(TextInputEditText textInputEditText, TextInputEditText textInputEditText2, final MainActivity mainActivity, View view) {
        Editable text = textInputEditText.getText();
        final String string = text != null ? text.toString() : null;
        if (string == null) {
            string = "";
        }
        Editable text2 = textInputEditText2.getText();
        String string2 = text2 != null ? text2.toString() : null;
        final String str = string2 != null ? string2 : "";
        if (string.length() == 0 || str.length() == 0) {
            onCreate$showToast(mainActivity, "⚠️ Please enter username and password");
        } else {
            mainActivity.executor.execute(new Runnable() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.onCreate$lambda$18$lambda$17(this.f$0, string, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$18$lambda$17(MainActivity mainActivity, String str, String str2) {
        try {
            TcpClient tcpClient = mainActivity.client;
            if (tcpClient != null) {
                tcpClient.register(str, str2);
            }
            onCreate$showToast(mainActivity, "✅ Registration request sent");
        } catch (Exception e) {
            onCreate$showToast(mainActivity, "❌ Register failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$20(TextInputEditText textInputEditText, TextInputEditText textInputEditText2, final MainActivity mainActivity, View view) {
        Editable text = textInputEditText.getText();
        final String string = text != null ? text.toString() : null;
        if (string == null) {
            string = "";
        }
        Editable text2 = textInputEditText2.getText();
        String string2 = text2 != null ? text2.toString() : null;
        final String str = string2 != null ? string2 : "";
        if (string.length() == 0 || str.length() == 0) {
            onCreate$showToast(mainActivity, "⚠️ Please enter username and password");
        } else {
            mainActivity.currentUsername = string;
            mainActivity.executor.execute(new Runnable() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda18
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.onCreate$lambda$20$lambda$19(this.f$0, string, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$20$lambda$19(MainActivity mainActivity, String str, String str2) {
        try {
            TcpClient tcpClient = mainActivity.client;
            if (tcpClient != null) {
                tcpClient.login(str, str2);
            }
        } catch (Exception e) {
            onCreate$showToast(mainActivity, "❌ Login request failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$25(final MainActivity mainActivity, View view) {
        PopupMenu popupMenu = new PopupMenu(mainActivity, view);
        popupMenu.getMenu().add("👥 Online Users");
        popupMenu.getMenu().add("📡 Ping");
        popupMenu.getMenu().add("🚪 Logout");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda0
            @Override // android.widget.PopupMenu.OnMenuItemClickListener
            public final boolean onMenuItemClick(MenuItem menuItem) {
                return MainActivity.onCreate$lambda$25$lambda$24(this.f$0, menuItem);
            }
        });
        popupMenu.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean onCreate$lambda$25$lambda$24(final MainActivity mainActivity, MenuItem menuItem) {
        CharSequence title = menuItem.getTitle();
        if (Intrinsics.areEqual(title, "👥 Online Users")) {
            mainActivity.executor.execute(new Runnable() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.onCreate$lambda$25$lambda$24$lambda$21(this.f$0);
                }
            });
            return true;
        }
        if (Intrinsics.areEqual(title, "📡 Ping")) {
            mainActivity.executor.execute(new Runnable() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.onCreate$lambda$25$lambda$24$lambda$22(this.f$0);
                }
            });
            return true;
        }
        if (!Intrinsics.areEqual(title, "🚪 Logout")) {
            return false;
        }
        mainActivity.executor.execute(new Runnable() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.onCreate$lambda$25$lambda$24$lambda$23(this.f$0);
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$25$lambda$24$lambda$21(MainActivity mainActivity) {
        try {
            TcpClient tcpClient = mainActivity.client;
            if (tcpClient != null) {
                tcpClient.listOnline();
            }
        } catch (Exception e) {
            onCreate$showToast(mainActivity, "❌ Request failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$25$lambda$24$lambda$22(MainActivity mainActivity) {
        try {
            TcpClient tcpClient = mainActivity.client;
            if (tcpClient != null) {
                tcpClient.ping();
            }
        } catch (Exception e) {
            onCreate$showToast(mainActivity, "❌ Ping failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$25$lambda$24$lambda$23(MainActivity mainActivity) {
        try {
            TcpClient tcpClient = mainActivity.client;
            if (tcpClient != null) {
                tcpClient.logout();
            }
        } catch (Exception e) {
            onCreate$showToast(mainActivity, "❌ Logout failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$28(final TextInputEditText textInputEditText, final MainActivity mainActivity, final LinearLayout linearLayout, final ScrollView scrollView, View view) {
        Editable text = textInputEditText.getText();
        String string = text != null ? text.toString() : null;
        if (string == null) {
            string = "";
        }
        final String str = string;
        if (str.length() == 0) {
            onCreate$showToast(mainActivity, "⚠️ Please enter a message");
        } else {
            mainActivity.executor.execute(new Runnable() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.onCreate$lambda$28$lambda$27(this.f$0, str, linearLayout, scrollView, textInputEditText);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$28$lambda$27(MainActivity mainActivity, String str, LinearLayout linearLayout, ScrollView scrollView, final TextInputEditText textInputEditText) {
        MainActivity mainActivity2;
        Exception exc;
        try {
            TcpClient tcpClient = mainActivity.client;
            if (tcpClient != null) {
                try {
                    tcpClient.sendMessage(str);
                } catch (Exception e) {
                    exc = e;
                    mainActivity2 = mainActivity;
                    onCreate$showToast(mainActivity2, "❌ Send failed: " + exc.getMessage());
                }
            }
            mainActivity2 = mainActivity;
            try {
                onCreate$addChatMessage$default(mainActivity2, linearLayout, scrollView, str, true, false, 32, null);
                mainActivity2.uiHandler.post(new Runnable() { // from class: com.vsl.massenger.MainActivity$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        MainActivity.onCreate$lambda$28$lambda$27$lambda$26(textInputEditText);
                    }
                });
            } catch (Exception e2) {
                e = e2;
                exc = e;
                onCreate$showToast(mainActivity2, "❌ Send failed: " + exc.getMessage());
            }
        } catch (Exception e3) {
            e = e3;
            mainActivity2 = mainActivity;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$28$lambda$27$lambda$26(TextInputEditText textInputEditText) {
        Editable text = textInputEditText.getText();
        if (text != null) {
            text.clear();
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        this.executor.shutdownNow();
        TcpClient tcpClient = this.client;
        if (tcpClient != null) {
            tcpClient.close();
        }
        super.onDestroy();
    }
}