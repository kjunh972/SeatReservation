import 'package:flutter/material.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'dart:io';
import 'package:permission_handler/permission_handler.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';

// 앱 시작점
void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // iOS 권한 요청
  if (Platform.isIOS) {
    await Permission.camera.request();
    await Permission.microphone.request();
  }

  runApp(const MyApp());
}

// 앱의 루트 위젯
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

// 홈페이지 StatefulWidget
class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

// 홈페이지 State 클래스
class _MyHomePageState extends State<MyHomePage> {
  // WebView 관련 변수
  final GlobalKey webViewKey = GlobalKey();
  InAppWebViewController? webViewController;
  String url = 'http://192.168.45.134:8055/';

  // 상태 관리 변수
  bool isLoading = true;
  double _loadingOpacity = 1.0;

  // 로딩 인디케이터 위젯
  Widget _buildLoadingIndicator() {
    return AnimatedOpacity(
      opacity: _loadingOpacity,
      duration: const Duration(milliseconds: 300),
      child: Container(
        width: double.infinity,
        height: double.infinity,
        color: Colors.white,
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              SpinKitFadingCircle(
                color: Colors.blue,
                size: 50.0,
              ),
              const SizedBox(height: 20),
              const Text(
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

  @override
  Widget build(BuildContext context) {
    final screenHeight = MediaQuery.of(context).size.height;
    final topPadding = screenHeight * 0.06;

    return PopScope(
      canPop: false,
      // 뒤로가기 처리
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
                  
                  localStorage.removeItem('openSidebar');
                })();
              """);

              // 뒤로가기 실행
              await Future.delayed(const Duration(milliseconds: 100));
              await webViewController!.goBack();

              // 사이드바 상태 확인
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
        body: Stack(
          children: [
            // WebView
            Padding(
              padding: EdgeInsets.only(top: topPadding),
              child: InAppWebView(
                key: webViewKey,
                initialUrlRequest: URLRequest(
                  url: WebUri(url),
                ),
                // WebView 초기 설정
                initialSettings: InAppWebViewSettings(
                  javaScriptEnabled: true,
                  mediaPlaybackRequiresUserGesture: false,
                  allowsInlineMediaPlayback: true,
                  mixedContentMode: MixedContentMode.MIXED_CONTENT_ALWAYS_ALLOW,
                  useWideViewPort: false,
                ),
                // WebView 생성 완료 시
                onWebViewCreated: (controller) {
                  webViewController = controller;
                  setState(() => isLoading = true);
                },
                // 페이지 로드 시작 시
                onLoadStart: (controller, url) {
                  setState(() {
                    isLoading = true;
                    _loadingOpacity = 1.0;
                  });
                },
                // 페이지 로드 완료 시
                onLoadStop: (controller, url) async {
                  setState(() {
                    isLoading = false;
                    _loadingOpacity = 0.0;
                  });

                  // 사이드바 상태 초기화
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
                // 에러 발생 시
                onReceivedError: (controller, request, error) {
                  if (error.type == WebResourceErrorType.CANCELLED) {
                    return;
                  }
                  setState(() => isLoading = false);

                  // 에러 메시지 표시
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text('오류: ${error.description}'),
                      duration: const Duration(seconds: 3),
                      action: SnackBarAction(
                        label: '다시 시도',
                        onPressed: () {
                          webViewController?.reload();
                        },
                      ),
                    ),
                  );
                },
              ),
            ),
            // 로딩 인디케이터
            if (isLoading) _buildLoadingIndicator(),
          ],
        ),
      ),
    );
  }
}