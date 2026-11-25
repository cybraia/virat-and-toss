# SMS Forwarder (Android)

Forwards any incoming SMS containing a keyword (default `SBM`, case-insensitive)
to another number, automatically, in the background.

## Build
1. Open the `sms-forwarder` folder in Android Studio and let Gradle sync.
2. Run on your phone (USB debugging), or Build > Build APK(s) and install it.

## Use
1. Enter the destination number (with country code) and keyword, tap **Save**.
2. Turn on **Forwarding enabled** and grant the SMS permissions.

Forwarded text looks like: `From <sender>:` followed by the original message.

## Notes
- Android only; iOS does not allow this.
- Each forward is sent as a normal SMS from your SIM, so your carrier may charge for it.
- Messages from the destination number itself are skipped to prevent loops.
- Some phones (Xiaomi, Oppo, Vivo, Samsung) kill background apps: set the app's
  battery to "Unrestricted" / allow autostart. If sideloaded on Android 13+, you may
  need App info > ⋮ > "Allow restricted settings" before SMS permissions can be granted.
- Play Store restricts SMS permissions, so this is meant for personal sideloading.
