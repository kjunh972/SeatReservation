using System;
using System.Diagnostics; // 프로세스 및 프로세스 모듈에 액세스하기 위해 필요
using System.Drawing; // 그래픽 및 색상 관련 기능을 사용하기 위해 필요
using System.Runtime.InteropServices; // Windows API 호출을 위해 필요
using System.Windows.Forms; // 윈도우 폼 애플리케이션을 만들기 위해 필요
using MySql.Data.MySqlClient; // MySQL 데이터베이스에 연결하기 위해 필요

namespace lockScreen
{
    internal static class Program
    {
        [STAThread] // 단일 스레드 아파트 모델을 사용하도록 지정
        static void Main()
        {
            Application.EnableVisualStyles(); // 시각적 스타일을 사용하도록 설정
            Application.SetCompatibleTextRenderingDefault(false); // 호환성 텍스트 렌더링 비활성화
            Application.Run(new LockScreenForm()); // LockScreenForm을 실행하도록 설정
        }
    }

    public class LockScreenForm : Form
    {
        // Windows API 함수들을 선언
        [DllImport("user32.dll")]
        private static extern bool SetForegroundWindow(IntPtr hWnd); // 지정한 창을 가장 위에 표시하도록 설정

        [DllImport("user32.dll")]
        private static extern IntPtr GetForegroundWindow(); // 현재 활성화된 창의 핸들을 가져오도록 설정

        [DllImport("user32.dll")]
        private static extern bool ShowWindow(IntPtr hWnd, int nCmdShow); // 창을 특정 상태로 표시하도록 설정

        [DllImport("user32.dll")]
        private static extern IntPtr SetWindowsHookEx(int idHook, LowLevelKeyboardProc lpfn, IntPtr hMod, uint dwThreadId); // 전역 키보드 훅을 설정

        [DllImport("user32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        private static extern bool UnhookWindowsHookEx(IntPtr hhk); // 설정된 훅을 제거하도록 설정

        [DllImport("user32.dll")]
        private static extern IntPtr CallNextHookEx(IntPtr hhk, int nCode, IntPtr wParam, IntPtr lParam); // 다음 훅 프로시저를 호출하도록 설정

        [DllImport("kernel32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        private static extern IntPtr GetModuleHandle(string lpModuleName); // 현재 프로세스의 모듈 핸들을 가져오도록 설정

        // 키보드 훅에 사용할 델리게이트를 선언
        private delegate IntPtr LowLevelKeyboardProc(int nCode, IntPtr wParam, IntPtr lParam);

        // 전역 키보드 훅을 설정하기 위한 상수들을 선언
        private const int WH_KEYBOARD_LL = 13; // 전역 키보드 훅 코드 설정
        private const int WM_KEYDOWN = 0x0100; // 키 다운 메시지 코드 설정
        private const int WM_SYSKEYDOWN = 0x0104; // 시스템 키 다운 메시지 코드 설정

        private LowLevelKeyboardProc _proc; // 훅 콜백 함수 설정
        private IntPtr _hookID = IntPtr.Zero; // 훅 핸들을 저장하도록 설정

        // 창 상태를 설정하기 위한 상수들을 선언
        private const int SW_RESTORE = 9; // 창을 복원 상태로 설정
        private const int SW_SHOWMAXIMIZED = 3; // 창을 최대화 상태로 설정
        private const int SW_SHOWNORMAL = 1; // 창을 기본 상태로 설정

        // 폼이 포그라운드에 있는지 확인하는 타이머를 설정
        private Timer checkForegroundTimer;
        private TextBox passwordTextBox; // 비밀번호 입력을 위한 텍스트 박스를 설정
        private MySqlConnection connection; // MySQL 연결 객체 설정

        public LockScreenForm()
        {
            _proc = HookCallback; // 훅 콜백 함수 설정

            // 폼의 제목을 "LOCK SCREEN"으로 설정
            this.Text = "LOCK SCREEN";
            // 폼의 테두리 스타일을 없애고 창이 최대화된 상태로 시작하도록 설정
            this.FormBorderStyle = FormBorderStyle.None;
            this.WindowState = FormWindowState.Maximized;
            // 폼이 닫힐 때 CustomForm_FormClosing 메서드를 호출하도록 설정
            this.FormClosing += CustomForm_FormClosing;
            // 폼이 항상 다른 창 위에 표시되도록 설정
            this.TopMost = true;

            // 배경 이미지를 설정
            string imagePath = @"..\..\..\Images\yuhan.png";
            this.BackgroundImage = Image.FromFile(imagePath); // 지정한 경로의 이미지를 배경으로 설정
            this.BackgroundImageLayout = ImageLayout.Stretch; // 배경 이미지를 폼에 맞게 늘려 표시하도록 설정

            // 비밀번호 입력 상자를 생성하고 설정
            this.passwordTextBox = new TextBox();
            // 비밀번호 입력 시 입력 내용을 별표로 표시하도록 설정
            this.passwordTextBox.PasswordChar = '*';
            // 비밀번호 입력 상자의 텍스트를 가운데 정렬
            this.passwordTextBox.TextAlign = HorizontalAlignment.Center;
            // 비밀번호 입력 상자의 크기를 설정
            this.passwordTextBox.Size = new Size(250, 50);
            // 비밀번호 입력 상자의 위치를 설정
            this.passwordTextBox.Location = new Point((this.ClientSize.Width - this.passwordTextBox.Width) * 22, (this.ClientSize.Height - this.passwordTextBox.Height) * 2);
            // Alt + F4 키 눌렸는지 확인하는 핸들러.
            this.passwordTextBox.KeyDown += PasswordTextBox_KeyDown;
            // 폼에 비밀번호 입력 상자를 추가
            this.Controls.Add(this.passwordTextBox);
            // 비밀번호 입력 상자를 폼의 가장 위에 표시
            this.passwordTextBox.BringToFront();

            // MySQL 연결 문자열을 설정
            string connectionString = "server=yuhan.clu0u0yy2ir8.ap-northeast-2.rds.amazonaws.com;database=YuhanDB;uid=root;pwd=rootroot;";
            connection = new MySqlConnection(connectionString); // MySQL 연결 객체를 초기화하도록 설정

            // 마우스 제약 및 타이머를 설정
            this.Load += LockScreenForm_Load; // 폼이 로드될 때 이벤트를 처리하도록 설정
            this.SizeChanged += LockScreenForm_SizeChanged; // 폼의 크기가 변경될 때 이벤트를 처리하도록 설정

            checkForegroundTimer = new Timer(); // 타이머 객체를 생성
            checkForegroundTimer.Interval = 500; // 0.5초마다 체크하도록 설정
            checkForegroundTimer.Tick += CheckForegroundTimer_Tick; // 타이머가 틱할 때 이벤트를 처리하도록 설정
            checkForegroundTimer.Start(); // 타이머를 시작하도록 설정

            // 키보드 훅을 설치하도록 설정
            _hookID = SetHook(_proc);
        }

        private void LockScreenForm_Load(object sender, EventArgs e)
        {
            UpdateMouseRestriction(); // 마우스 제한 영역을 업데이트하도록 설정
        }

        private void LockScreenForm_SizeChanged(object sender, EventArgs e)
        {
            UpdateMouseRestriction(); // 폼의 크기가 변경될 때 마우스 제한 영역을 업데이트하도록 설정
        }

        private void UpdateMouseRestriction()
        {
            Rectangle rect = this.RectangleToScreen(this.ClientRectangle); // 폼의 클라이언트 영역을 스크린 좌표로 변환
            Cursor.Clip = rect; // 마우스 커서를 폼의 클라이언트 영역 내로 제한하도록 설정
        }

        private void PasswordTextBox_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Enter) // Enter 키가 눌렸을 때
            {
                if (IsPasswordCorrect()) // 입력된 비밀번호가 올바른지 확인
                {
                    MessageBox.Show("비밀번호가 확인되었습니다."); // 비밀번호가 올바르면 메시지 박스를 표시
                    this.Close(); // 폼을 닫도록 설정
                }
                else
                {
                    MessageBox.Show("비밀번호가 올바르지 않습니다."); // 비밀번호가 틀리면 메시지 박스를 표시
                    this.passwordTextBox.Clear(); // 비밀번호 입력 상자를 초기화하도록 설정
                }
            }
        }

        private bool IsPasswordCorrect()
        {
            try
            {
                connection.Open(); // 데이터베이스 연결을 열도록 설정
                string query = "SELECT pass FROM Yuhan WHERE id = 'kjunh972';"; // 비밀번호를 확인하기 위한 SQL 쿼리 설정
                MySqlCommand command = new MySqlCommand(query, connection); // SQL 명령 객체를 생성
                object result = command.ExecuteScalar(); // 쿼리 결과를 가져오도록 설정
                connection.Close(); // 데이터베이스 연결을 닫도록 설정

                return result != null && result.ToString() == passwordTextBox.Text; // 입력된 비밀번호와 데이터베이스의 비밀번호를 비교하도록 설정
            }
            catch (Exception ex)
            {
                MessageBox.Show("데이터베이스 연결에 실패했습니다: " + ex.Message); // 데이터베이스 연결 실패 시 예외 메시지 표시
                return false;
            }
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            base.OnPaint(e); // 기본 페인트 작업을 수행하도록 설정
            //하얀색 브러쉬를 사용해 불투명하게 만들기 위해 설정
            using (SolidBrush brush = new SolidBrush(Color.FromArgb(170, Color.White))) // 반투명 흰색 브러시를 생성
            {
                Rectangle squareOp = new Rectangle(645, 400, this.ClientSize.Width / 4, 200); // 사각형 영역을 정의
                e.Graphics.FillRectangle(brush, squareOp); // 정의된 영역을 브러시로 채우도록 설정
            }
        }

        private void CustomForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (e.CloseReason == CloseReason.UserClosing && !IsPasswordCorrect()) // 사용자가 폼을 닫으려고 할 때 비밀번호가 올바르지 않으면
            {
                e.Cancel = true; // 폼이 닫히지 않도록 설정
            }
        }

        private void CheckForegroundTimer_Tick(object sender, EventArgs e)
        {
            IntPtr foregroundWindow = GetForegroundWindow(); // 현재 포그라운드에 있는 창의 핸들을 가져오도록 설정
            if (foregroundWindow != this.Handle) // 현재 폼이 포그라운드에 있지 않으면
            {
                // 폼이 포그라운드에 없으면 다시 최상단으로 가져오도록 설정
                SetForegroundWindow(this.Handle); // 폼을 포그라운드로 설정
                ShowWindow(this.Handle, SW_SHOWMAXIMIZED); // 폼을 최대화 상태로 표시하도록 설정
            }
        }

        // 키보드 훅을 설치하도록 설정
        private IntPtr SetHook(LowLevelKeyboardProc proc)
        {
            using (Process curProcess = Process.GetCurrentProcess()) // 현재 프로세스를 가져오도록 설정
            using (ProcessModule curModule = curProcess.MainModule) // 현재 프로세스의 모듈을 가져오도록 설정
            {
                return SetWindowsHookEx(WH_KEYBOARD_LL, proc, GetModuleHandle(curModule.ModuleName), 0); // 전역 키보드 훅을 설정
            }
        }

        // 키보드 훅 콜백 함수 설정
        private IntPtr HookCallback(int nCode, IntPtr wParam, IntPtr lParam)
        {
            if (nCode >= 0 && (wParam == (IntPtr)WM_KEYDOWN || wParam == (IntPtr)WM_SYSKEYDOWN)) // 키가 눌렸을 때 처리
            {
                int vkCode = Marshal.ReadInt32(lParam); // 눌린 키의 가상 키 코드를 가져오도록 설정

                // Ctrl, Alt, Delete 키 입력을 차단하도록 설정
                if (vkCode == (int)Keys.ControlKey || vkCode == (int)Keys.Menu || vkCode == (int)Keys.Delete)
                {
                    return (IntPtr)1; // 키 입력을 무시하도록 설정
                }
            }
            return CallNextHookEx(_hookID, nCode, wParam, lParam); // 다음 훅으로 메시지를 전달하도록 설정
        }

        protected override void OnFormClosing(FormClosingEventArgs e)
        {
            UnhookWindowsHookEx(_hookID); // 폼이 닫힐 때 훅을 해제하도록 설정
            base.OnFormClosing(e); // 기본 폼 닫기 동작을 수행하도록 설정
        }

        // WM_SYSCOMMAND 메시지를 처리하여 Alt+Tab, Alt+F4 등을 방지하도록 설정
        protected override void WndProc(ref Message m)
        {
            const int WM_SYSCOMMAND = 0x0112; // 시스템 명령 메시지 설정
            const int SC_MINIMIZE = 0xF020; // 최소화 명령 코드 설정
            const int SC_MAXIMIZE = 0xF030; // 최대화 명령 코드 설정
            const int SC_CLOSE = 0xF060; // 닫기 명령 코드 설정

            if (m.Msg == WM_SYSCOMMAND) // 시스템 명령 메시지가 발생했을 때 처리
            {
                int command = m.WParam.ToInt32() & 0xFFF0; // 명령 코드를 확인하도록 설정
                if (command == SC_MINIMIZE || command == SC_CLOSE || command == SC_MAXIMIZE) // 최소화, 닫기, 최대화 명령이 발생하면
                {
                    // Alt+Tab, Alt+F4, 윈도우 키 등을 막기 위해 메시지를 무시하도록 설정
                    return;
                }
            }
            base.WndProc(ref m); // 기본 메시지 처리 동작을 수행하도록 설정
        }
    }
}
