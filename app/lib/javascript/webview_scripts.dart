class WebViewScripts {
  static String get sessionCheckScript => '''
    (function() {
      const errorElement = document.querySelector('.error');
      if (errorElement && errorElement.textContent.includes('세션이 만료되었습니다')) {
        Swal.fire({
          icon: 'error',
          title: '알림',
          text: errorElement.textContent,
          confirmButtonText: '확인',
          allowOutsideClick: false,
          focusConfirm: true,
          customClass: {
            popup: 'timetable-popup',
            confirmButton: 'timetable-confirm'
          },
          didOpen: () => {
            const confirmButton = Swal.getConfirmButton();
            if (confirmButton) {
              confirmButton.focus();
            }
          }
        }).then((result) => {
          if (result.isConfirmed) {
            const mobileSidebar = document.getElementById('menu-mobile-sidebar');
            const overlay = document.getElementById('menu-overlay');
            const mobileToggle = document.getElementById('menu-mobile-toggle');
            
            if (mobileSidebar) mobileSidebar.classList.add('active');
            if (overlay) overlay.classList.add('active');
            if (mobileToggle) mobileToggle.classList.add('active');
          }
        });
        return true;
      }
      return false;
    })();
  ''';

  static String get navigationScript => '''
    (function() {
      // 기존 웹사이트의 이벤트 핸들러가 동작하게.
      const links = document.querySelectorAll('a');
      links.forEach(link => {
        link.addEventListener('click', (e) => {
          // 기본 동작 허용
          return true;
        });
      });
    })();
  ''';

  static String get schoolSearchScript => '''
    (function() {
      function enhanceSchoolSearch() {
        const schoolInput = document.getElementById('schoolInput');
        const schoolList = document.getElementById('schoolList');
        
        if (!schoolInput || !schoolList) return;
        
        const parentElement = schoolInput.parentElement;
        if (!parentElement) return;
        
        parentElement.style.position = 'relative';
        
        const schoolListContainer = document.createElement('div');
        schoolListContainer.id = 'schoolListContainer';
        schoolListContainer.style.cssText = 'position:absolute;width:100%;max-height:200px;overflow-y:auto;background:white;border:1px solid #ccc;border-radius:8px;margin-top:2px;top:100%;left:0;display:none;z-index:9999;box-shadow:0 2px 4px rgba(0,0,0,0.1)';
        
        const schools = ['가톨릭대학교', '고려대학교', '동양미래대학교', '서울대학교', 
                      '성공회대학교', '연세대학교', '유한대학교', '중앙대학교'];
        
        const fragment = document.createDocumentFragment();
        schools.forEach(school => {
          const item = document.createElement('div');
          item.textContent = school;
          item.style.cssText = 'padding:12px 16px;cursor:pointer;border-bottom:1px solid #eee;font-size:14px';
          
          item.addEventListener('mouseover', () => item.style.backgroundColor = '#f5f5f5');
          item.addEventListener('mouseout', () => item.style.backgroundColor = 'white');
          item.addEventListener('click', () => {
            schoolInput.value = school;
            schoolListContainer.style.display = 'none';
          });
          
          fragment.appendChild(item);
        });
        
        schoolListContainer.appendChild(fragment);
        schoolList.style.display = 'none';
        parentElement.appendChild(schoolListContainer);
        
        const showList = () => schoolListContainer.style.display = 'block';
        const hideList = (e) => {
          if (e.target !== schoolInput && e.target !== schoolListContainer) {
            schoolListContainer.style.display = 'none';
          }
        };
        
        schoolInput.addEventListener('click', (e) => {
          e.preventDefault();
          showList();
        });
        
        schoolInput.addEventListener('input', () => {
          const filter = schoolInput.value.toUpperCase();
          Array.from(schoolListContainer.children).forEach(item => {
            item.style.display = item.textContent.toUpperCase().includes(filter) ? 'block' : 'none';
          });
          showList();
        });
        
        document.addEventListener('click', hideList);
      }
      
      enhanceSchoolSearch();
    })();
  ''';
}
