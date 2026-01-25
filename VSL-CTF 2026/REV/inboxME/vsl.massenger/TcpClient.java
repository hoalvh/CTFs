package com.vsl.massenger;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.io.IOException;
import java.nio.ByteBuffer;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;

/* compiled from: TcpClient.kt */
@Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\u0004\b\t\u0010\nJ\u001e\u0010\u0010\u001a\u00020\u00112\u0016\b\u0002\u0010\u0012\u001a\u0010\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u0013J\u000e\u0010\u0014\u001a\u00020\u00112\u0006\u0010\u0015\u001a\u00020\u0003J\u0016\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0018\u001a\u00020\u0003J\u0016\u0010\u0019\u001a\u00020\u00112\u0006\u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0018\u001a\u00020\u0003J\u0006\u0010\u001a\u001a\u00020\u0011J\u0006\u0010\u001b\u001a\u00020\u0011J\u0006\u0010\u001c\u001a\u00020\u0011J\u0018\u0010\u001d\u001a\u00020\u00112\u0006\u0010\u001e\u001a\u00020\u00052\u0006\u0010\u001f\u001a\u00020\u0003H\u0002J\u0006\u0010 \u001a\u00020\u0011J\u001c\u0010!\u001a\u00020\u00112\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00110\u0013H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e¢\u0006\u0004\n\u0002\u0010\rR\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\""}, d2 = {"Lcom/vsl/massenger/TcpClient;", "", "host", "", "port", "", "pinnedSha256", "pinnedCert", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;[B)V", "handle", "", "Ljava/lang/Long;", "listenerThread", "Ljava/lang/Thread;", "connect", "", "onMessage", "Lkotlin/Function1;", "sendMessage", "text", "register", "username", "password", "login", "listOnline", "logout", "ping", "sendCommand", "command", "payload", "close", "startListener", "app_release"}, k = 1, mv = {2, 0, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class TcpClient {
    private Long handle;
    private final String host;
    private Thread listenerThread;
    private final byte[] pinnedCert;
    private final String pinnedSha256;
    private final int port;

    public TcpClient(String host, int i, String str, byte[] bArr) {
        Intrinsics.checkNotNullParameter(host, "host");
        this.host = host;
        this.port = i;
        this.pinnedSha256 = str;
        this.pinnedCert = bArr;
    }

    public /* synthetic */ TcpClient(String str, int i, String str2, byte[] bArr, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, i, (i2 & 4) != 0 ? null : str2, (i2 & 8) != 0 ? null : bArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ void connect$default(TcpClient tcpClient, Function1 function1, int i, Object obj) throws IOException {
        if ((i & 1) != 0) {
            function1 = null;
        }
        tcpClient.connect(function1);
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x0029  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void connect(kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> r9) throws java.io.IOException {
        /*
            r8 = this;
            java.lang.String r0 = r8.pinnedSha256
            if (r0 == 0) goto L29
            java.util.Locale r1 = java.util.Locale.ROOT
            java.lang.String r2 = r0.toLowerCase(r1)
            java.lang.String r0 = "toLowerCase(...)"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r2, r0)
            if (r2 == 0) goto L29
            r6 = 4
            r7 = 0
            java.lang.String r3 = ":"
            java.lang.String r4 = ""
            r5 = 0
            java.lang.String r0 = kotlin.text.StringsKt.replace$default(r2, r3, r4, r5, r6, r7)
            if (r0 == 0) goto L29
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            java.lang.CharSequence r0 = kotlin.text.StringsKt.trim(r0)
            java.lang.String r0 = r0.toString()
            goto L2a
        L29:
            r0 = 0
        L2a:
            if (r0 != 0) goto L2e
            java.lang.String r0 = ""
        L2e:
            byte[] r1 = r8.pinnedCert
            if (r1 != 0) goto L35
            r1 = 0
            byte[] r1 = new byte[r1]
        L35:
            com.vsl.massenger.NativeProtocol r2 = com.vsl.massenger.NativeProtocol.INSTANCE
            java.lang.String r3 = r8.host
            int r4 = r8.port
            long r0 = r2.connectAndHandshake(r3, r4, r0, r1)
            r2 = 0
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 == 0) goto L51
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            r8.handle = r0
            if (r9 == 0) goto L50
            r8.startListener(r9)
        L50:
            return
        L51:
            java.io.IOException r9 = new java.io.IOException
            java.lang.String r0 = "Native connect/handshake failed"
            r9.<init>(r0)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.vsl.massenger.TcpClient.connect(kotlin.jvm.functions.Function1):void");
    }

    public final synchronized void sendMessage(String text) throws IOException {
        Intrinsics.checkNotNullParameter(text, "text");
        sendCommand(2, text);
    }

    public final synchronized void register(String username, String password) throws IOException {
        Intrinsics.checkNotNullParameter(username, "username");
        Intrinsics.checkNotNullParameter(password, "password");
        sendCommand(3, username + ":" + password);
    }

    public final synchronized void login(String username, String password) throws IOException {
        Intrinsics.checkNotNullParameter(username, "username");
        Intrinsics.checkNotNullParameter(password, "password");
        sendCommand(4, username + ":" + password);
    }

    public final synchronized void listOnline() throws IOException {
        sendCommand(5, "");
    }

    public final synchronized void logout() throws IOException {
        sendCommand(6, "");
    }

    public final synchronized void ping() throws IOException {
        sendCommand(7, "");
    }

    private final void sendCommand(int command, String payload) throws IOException {
        Long l = this.handle;
        if (l == null) {
            throw new IllegalStateException("Not connected");
        }
        long jLongValue = l.longValue();
        byte[] bytes = payload.getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
        int iSendCommand = NativeProtocol.INSTANCE.sendCommand(jLongValue, command, bytes);
        if (iSendCommand != 0) {
            throw new IOException("Send failed (rc=" + iSendCommand + ")");
        }
    }

    public final void close() {
        try {
            Thread thread = this.listenerThread;
            if (thread != null) {
                thread.interrupt();
            }
            Long l = this.handle;
            if (l != null) {
                NativeProtocol.INSTANCE.close(l.longValue());
            }
        } catch (Exception unused) {
        }
        this.listenerThread = null;
        this.handle = null;
    }

    private final void startListener(final Function1<? super String, Unit> onMessage) {
        Long l = this.handle;
        if (l != null) {
            final long jLongValue = l.longValue();
            Thread thread = new Thread(new Runnable() { // from class: com.vsl.massenger.TcpClient$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TcpClient.startListener$lambda$1(jLongValue, onMessage);
                }
            });
            thread.start();
            this.listenerThread = thread;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void startListener$lambda$1(long j, Function1 function1) {
        byte[] bArrRecvInner;
        while (!Thread.currentThread().isInterrupted() && (bArrRecvInner = NativeProtocol.INSTANCE.recvInner(j)) != null) {
            try {
                ByteBuffer byteBufferWrap = ByteBuffer.wrap(bArrRecvInner);
                byteBufferWrap.get();
                int i = byteBufferWrap.getInt();
                if (i >= 0 && i <= byteBufferWrap.remaining()) {
                    byte[] bArr = new byte[i];
                    byteBufferWrap.get(bArr);
                    function1.invoke(new String(bArr, Charsets.UTF_8));
                }
            } catch (Exception unused) {
                return;
            }
        }
    }
}