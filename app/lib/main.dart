import 'package:flutter/material.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'dart:io';
import 'package:permission_handler/permission_handler.dart';

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

class _MyHomePageState extends State<MyHomePage> {
  final GlobalKey webViewKey = GlobalKey();
  InAppWebViewController? webViewController;
  String url = 'http://192.168.45.134:8055/';
  bool isLoading = true;

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
              // 사이드바 닫기
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
                  
                  // localStorage에서 사이드바 상태 제거
                  localStorage.removeItem('openSidebar');
                })();
              """);

              // 잠시 대기 후 뒤로가기
              await Future.delayed(const Duration(milliseconds: 100));
              await webViewController!.goBack();

              // 페이지 로드 후 사이드바 상태 다시 확인
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

              // 페이지 새로고침
              await Future.delayed(const Duration(milliseconds: 300));
              await webViewController!.reload();
            }
          } else {
            Navigator.of(context).pop();
          }
        }
      },
      child: Scaffold(
        body: Padding(
          padding: EdgeInsets.only(top: topPadding),
          child: Stack(
            children: [
              InAppWebView(
                key: webViewKey,
                initialUrlRequest: URLRequest(
                  url: WebUri(url),
                ),
                initialSettings: InAppWebViewSettings(
                  javaScriptEnabled: true,
                  mediaPlaybackRequiresUserGesture: false,
                  allowsInlineMediaPlayback: true,
                  mixedContentMode: MixedContentMode.MIXED_CONTENT_ALWAYS_ALLOW,
                  useWideViewPort: false,
                ),
                onWebViewCreated: (controller) {
                  webViewController = controller;
                  debugPrint('WebView created');
                },
                onLoadStart: (controller, url) {
                  setState(() => isLoading = true);
                  debugPrint('Page load started: $url');
                },
                onLoadStop: (controller, url) async {
                  setState(() => isLoading = false);
                  debugPrint('Page load finished: $url');

                  // 페이지 로드 완료 후 사이드바 상태 확인
                  await controller.evaluateJavascript(source: """
                    (function() {
                      const mobileSidebar = document.getElementById('menu-mobile-sidebar');
                      const overlay = document.getElementById('menu-overlay');
                      if (mobileSidebar && mobileSidebar.classList.contains('active')) {
                        mobileSidebar.classList.remove('active');
                      }
                      if (overlay && overlay.classList.contains('active')) {
                        overlay.classList.remove('active');
                      }
                    })();
                  """);
                },
                onReceivedError: (controller, request, error) {
                  if (error.type == WebResourceErrorType.CANCELLED) {
                    return;
                  }
                  setState(() => isLoading = false);

                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text('오류: ${error.description}'),
                      duration: const Duration(seconds: 3),
                    ),
                  );
                },
                onConsoleMessage: (controller, consoleMessage) {
                  debugPrint('Console: ${consoleMessage.message}');
                },
              ),
              if (isLoading)
                const Center(
                  child: CircularProgressIndicator(),
                ),
            ],
          ),
        ),
      ),
    );
  }
}