// lib/main.dart
import 'package:flutter/material.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'dart:io';
import 'package:permission_handler/permission_handler.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'javascript/webview_scripts.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  if (Platform.isIOS) {
    await Permission.camera.request();
    await Permission.microphone.request();
  }

  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '강의실 예약 시스템',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.white),
        scaffoldBackgroundColor: Colors.white,
        useMaterial3: true,
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> with WidgetsBindingObserver {
  final GlobalKey webViewKey = GlobalKey();
  InAppWebViewController? webViewController;
  final String url = 'http://10.107.0.37:8055/';
  bool isLoading = true;
  double _loadingOpacity = 1.0;

  final InAppWebViewSettings _webViewSettings = InAppWebViewSettings(
    javaScriptEnabled: true,
    mediaPlaybackRequiresUserGesture: false,
    allowsInlineMediaPlayback: true,
    mixedContentMode: MixedContentMode.MIXED_CONTENT_ALWAYS_ALLOW,
    useWideViewPort: true,
    loadWithOverviewMode: true,
    useShouldOverrideUrlLoading: true,
    useOnLoadResource: true,
    useShouldInterceptAjaxRequest: true,
    useShouldInterceptFetchRequest: true,
    cacheEnabled: false,
    clearCache: true,
    hardwareAcceleration: true,
    supportZoom: false,
    builtInZoomControls: false,
    displayZoomControls: false,
    initialScale: 1,
    minimumFontSize: 8,
    verticalScrollBarEnabled: false,
    horizontalScrollBarEnabled: false,
    overScrollMode: OverScrollMode.NEVER,
    disableContextMenu: true,
    disableDefaultErrorPage: true,
    transparentBackground: true,
    applicationNameForUserAgent: 'MyApp/2.0',
    incognito: true,
    preferredContentMode: UserPreferredContentMode.MOBILE,
    allowFileAccessFromFileURLs: false,
    allowUniversalAccessFromFileURLs: false,
  );

  final Map<String, String> _headers = {
    'Cache-Control': 'no-cache, no-store, must-revalidate',
    'Pragma': 'no-cache',
    'Expires': '0',
  };

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      webViewController?.reload();
    }
  }

  Widget _buildLoadingIndicator() {
    return AnimatedOpacity(
      opacity: _loadingOpacity,
      duration: const Duration(milliseconds: 300),
      child: Container(
        width: double.infinity,
        height: double.infinity,
        color: Colors.white,
        child: const Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              SpinKitFadingCircle(
                color: Colors.blue,
                size: 50.0,
              ),
              SizedBox(height: 20),
              Text(
                '로딩중...',
                style: TextStyle(
                  fontSize: 16,
                  color: Colors.black54,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _handleBackNavigation() async {
    if (!isLoading) {
      await webViewController?.goBack();
    }
  }

  @override
  Widget build(BuildContext context) {
    final screenHeight = MediaQuery.of(context).size.height;
    final topPadding = screenHeight * 0.06;

    return PopScope(
      canPop: false,
      onPopInvoked: (didPop) async {
        if (webViewController != null) {
          if (await webViewController!.canGoBack()) {
            if (!isLoading) {
              await webViewController!.evaluateJavascript(source: """
                (function() {
                  const mobileSidebar = document.getElementById('menu-mobile-sidebar');
                  const overlay = document.getElementById('menu-overlay');
                  const mobileToggle = document.getElementById('menu-mobile-toggle');
                  
                  if (mobileSidebar) {
                    mobileSidebar.classList.remove('active');
                  }
                  if (overlay) {
                    overlay.classList.remove('active');
                  }
                  if (mobileToggle) {
                    mobileToggle.style.transform = 'rotate(0deg)';
                  }
                  
                  localStorage.removeItem('openSidebar');
                })();
              """);

              await Future.delayed(const Duration(milliseconds: 100));
              await webViewController!.goBack();

              await Future.delayed(const Duration(milliseconds: 200));
              await webViewController!.evaluateJavascript(source: """
                (function() {
                  const mobileSidebar = document.getElementById('menu-mobile-sidebar');
                  const overlay = document.getElementById('menu-overlay');
                  if (mobileSidebar) {
                    mobileSidebar.classList.remove('active');
                  }
                  if (overlay) {
                    overlay.classList.remove('active');
                  }
                })();
              """);

              await Future.delayed(const Duration(milliseconds: 300));
              await webViewController!.reload();
            }
          } else {
            if (context.mounted) {
              Navigator.of(context).pop();
            }
          }
        }
      },
      child: Scaffold(
        body: Stack(
          children: [
            Padding(
              padding: EdgeInsets.only(
                top: MediaQuery.of(context).padding.top,
              ),
              child: InAppWebView(
                key: webViewKey,
                initialUrlRequest: URLRequest(
                  url: WebUri(url),
                  headers: _headers,
                ),
                initialSettings: _webViewSettings,
                onWebViewCreated: (controller) {
                  webViewController = controller;
                  setState(() => isLoading = true);
                },
                onLoadStart: (controller, url) {
                  setState(() {
                    isLoading = true;
                    _loadingOpacity = 1.0;
                  });
                },
                onLoadStop: (controller, url) async {
                  setState(() {
                    isLoading = false;
                    _loadingOpacity = 0.0;
                  });

                  await controller.evaluateJavascript(
                    source: WebViewScripts.sessionCheckScript,
                  );
                  await controller.evaluateJavascript(
                    source: WebViewScripts.schoolSearchScript,
                  );
                  await controller.evaluateJavascript(
                    source: WebViewScripts.navigationScript,
                  );
                },
                onProgressChanged: (controller, progress) {
                  if (progress == 100) {
                    setState(() {
                      isLoading = false;
                      _loadingOpacity = 0.0;
                    });
                  }
                },
                shouldOverrideUrlLoading: (controller, navigationAction) async {
                  return NavigationActionPolicy.ALLOW;
                },
                onReceivedError: (controller, request, error) async {
                  if (error.type == WebResourceErrorType.CANCELLED) return;

                  setState(() => isLoading = false);

                  if (!context.mounted) return;

                  if (error.type == WebResourceErrorType.TIMEOUT ||
                      error.type == WebResourceErrorType.HOST_LOOKUP) {
                    await Future.delayed(const Duration(seconds: 2));
                    await webViewController?.reload();
                  } else {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: const Text('연결 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'),
                        duration: const Duration(seconds: 3),
                        action: SnackBarAction(
                          label: '다시 시도',
                          onPressed: () async {
                            await webViewController?.reload();
                          },
                        ),
                      ),
                    );
                  }
                },
              ),
            ),
            if (isLoading) _buildLoadingIndicator(),
          ],
        ),
      ),
    );
  }
}