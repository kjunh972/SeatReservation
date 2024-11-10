import 'package:flutter/material.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'dart:io';
import 'package:permission_handler/permission_handler.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';

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
  final String url = 'http://192.168.45.134:8055/';
  bool isLoading = true;
  double _loadingOpacity = 1.0;

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
              padding: EdgeInsets.only(top: topPadding),
              child: InAppWebView(
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
                  supportZoom: true,
                  builtInZoomControls: true,
                  displayZoomControls: false,
                  textZoom: 100,
                  allowsLinkPreview: false,
                  disableContextMenu: false,
                  disableDefaultErrorPage: false,
                  enableViewportScale: false,
                  transparentBackground: true,
                  disallowOverScroll: true,
                ),
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

                      function enhanceSchoolSearch() {
                        const schoolInput = document.getElementById('schoolInput');
                        const schoolList = document.getElementById('schoolList');
                        
                        if (!schoolInput || !schoolList) return;
                        
                        const schoolListContainer = document.createElement('div');
                        schoolListContainer.id = 'schoolListContainer';
                        schoolListContainer.style.cssText = `
                          position: absolute;
                          width: calc(100% - 2rem);
                          max-height: 200px;
                          overflow-y: auto;
                          background: white;
                          border: 1px solid #ccc;
                          border-radius: 8px;
                          margin-top: 63px; 
                          top: 0; 
                          left: 0; 
                          display: none;
                          z-index: 9999;
                          box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        `;
                        
                        const schools = ['가톨릭대학교', '고려대학교', '동양미래대학교', '서울대학교', 
                                      '성공회대학교', '연세대학교', '유한대학교', '중앙대학교'];
                        
                        schools.forEach(school => {
                          const item = document.createElement('div');
                          item.textContent = school;
                          item.style.cssText = `
                            padding: 12px;
                            cursor: pointer;
                            border-bottom: 1px solid #eee;
                          `;
                          
                          item.addEventListener('click', () => {
                            schoolInput.value = school;
                            schoolListContainer.style.display = 'none';
                          });
                          
                          schoolListContainer.appendChild(item);
                        });
                        
                        schoolList.style.display = 'none';
                        schoolInput.parentNode.appendChild(schoolListContainer);
                        
                        schoolInput.addEventListener('click', (e) => {
                          e.preventDefault();
                          schoolListContainer.style.display = 'block';
                        });
                        
                        schoolInput.addEventListener('input', () => {
                          const filter = schoolInput.value.toUpperCase();
                          Array.from(schoolListContainer.children).forEach(item => {
                            const text = item.textContent || item.innerText;
                            item.style.display = text.toUpperCase().includes(filter) ? 'block' : 'none';
                          });
                          schoolListContainer.style.display = 'block';
                        });
                        
                        document.addEventListener('click', (e) => {
                          if (e.target !== schoolInput && e.target !== schoolListContainer) {
                            schoolListContainer.style.display = 'none';
                          }
                        });
                      }
                      
                      enhanceSchoolSearch();
                    })();
                  """);
                },
                onReceivedError: (controller, request, error) {
                  if (error.type == WebResourceErrorType.CANCELLED) {
                    return;
                  }
                  setState(() => isLoading = false);

                  if (context.mounted) {
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