using System;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text.RegularExpressions;
using System.Windows.Forms;
using MySql.Data.MySqlClient;

namespace lockScreen
{
    internal static class Program
    {
        [STAThread]
        static void Main()
        {
            // 애플리케이션의 비주얼 스타일을 활성화하고 텍스트 렌더링을 설정합니다.
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            // LockScreenForm을 실행하여 폼을 표시합니다.
            Application.Run(new LockScreenForm());
        }
    }

    public class LockScreenForm : Form
    {
        [DllImport("user32.dll")]
        private static extern bool SetForegroundWindow(IntPtr hWnd);

        [DllImport("user32.dll")]
        private static extern IntPtr GetForegroundWindow();

        [DllImport("user32.dll")]
        private static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);

        [DllImport("user32.dll")]
        private static extern IntPtr SetWindowsHookEx(int idHook, LowLevelKeyboardProc lpfn, IntPtr hMod, uint dwThreadId);

        [DllImport("user32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        private static extern bool UnhookWindowsHookEx(IntPtr hhk);

        [DllImport("user32.dll")]
        private static extern IntPtr CallNextHookEx(IntPtr hhk, int nCode, IntPtr wParam, IntPtr lParam);

        [DllImport("kernel32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        private static extern IntPtr GetModuleHandle(string lpModuleName);

        // LowLevelKeyboardProc 델리게이트 정의: 키보드 훅 콜백 메서드를 지정합니다.
        private delegate IntPtr LowLevelKeyboardProc(int nCode, IntPtr wParam, IntPtr lParam);

        // 훅 ID 및 키보드 메시지에 대한 상수 정의
        private const int WH_KEYBOARD_LL = 13; // 저수준 키보드 훅
        private const int WM_KEYDOWN = 0x0100; // 키가 눌렸을 때 메시지
        private const int WM_SYSKEYDOWN = 0x0104; // 시스템 키가 눌렸을 때 메시지

        private LowLevelKeyboardProc _proc; // 키보드 훅 콜백 메서드
        private IntPtr _hookID = IntPtr.Zero; // 훅 ID

        // 윈도우 상태에 대한 상수 정의
        private const int SW_RESTORE = 9; // 윈도우 복원
        private const int SW_SHOWMAXIMIZED = 3; // 윈도우 최대화
        private const int SW_SHOWNORMAL = 1; // 윈도우 보통 상태로 표시

        private Timer checkForegroundTimer; // 포그라운드 창 확인 타이머
        private TextBox numTextBox; // 좌석 예약 번호 입력 상자
        private TextBox nameTextBox; // 학생 이름 입력 상자
        private TextBox studentIdTextBox; // 학번 입력 상자
        private MySqlConnection connection; // MySQL 데이터베이스 연결
        private Button submitButton; // 확인 버튼

        // 데이터베이스 연결 설정
        string connectionString = "server=localhost;database=YuhanDB;uid=root;pwd=root;";

        // 컴퓨터 이름 및 좌석 번호를 추출합니다.
        private static string computerName = SystemInformation.ComputerName;
        private string classroomName = new string(computerName.Where(char.IsDigit).Take(4).ToArray());
        private int seat = int.Parse(Regex.Match(computerName, @"_(\d+)$").Groups[1].Value);

        public LockScreenForm()
        {
            _proc = HookCallback; // 키보드 훅 콜백 메서드 설정

            this.Text = "LOCK SCREEN"; // 폼 제목 설정
            this.FormBorderStyle = FormBorderStyle.None; // 폼의 테두리 제거
            this.WindowState = FormWindowState.Maximized; // 폼을 최대화 상태로 설정
            this.FormClosing += CustomForm_FormClosing; // 폼이 닫힐 때 호출될 이벤트 핸들러 추가
            this.TopMost = true; // 폼을 항상 최상위로 설정

            // 배경 이미지 설정
            try
            {
                // 현재 실행 파일의 디렉토리를 기준으로 이미지 경로 설정
                string baseDirectory = AppDomain.CurrentDomain.BaseDirectory;
                string imagePath = Path.Combine(baseDirectory, $@"..\..\..\Images\yuhan{seat}.png");

                // 디버그용 메시지
                Console.WriteLine($"이미지 경로: {imagePath}");

                if (File.Exists(imagePath))
                {
                    using (var stream = new FileStream(imagePath, FileMode.Open, FileAccess.Read))
                    {
                        this.BackgroundImage = Image.FromStream(stream);
                    }
                    this.BackgroundImageLayout = ImageLayout.Stretch;
                }
                else
                {
                    MessageBox.Show($"좌석 {seat}에 해당하는 이미지를 찾을 수 없습니다.\n경로: {imagePath}");
                    // 기본 이미지 설정이 필요한 경우 여기에 추가
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"이미지 로드 중 오류 발생: {ex.Message}");
            }

            // 학생 이름 입력 상자 생성 및 설정
            this.nameTextBox = CreatePlaceholderTextBox("학생 이름", new Point((this.ClientSize.Width - 200) * 10, (this.ClientSize.Height - 150) * 3));

            // 학번 입력 상자 생성 및 설정
            this.studentIdTextBox = CreatePlaceholderTextBox("학번", new Point((this.ClientSize.Width - 200) * 10, (this.ClientSize.Height - 70) * 3));

            // 좌석 예약 번호 입력 상자 생성 및 설정
            this.numTextBox = CreatePlaceholderTextBox("좌석 예약 번호", new Point((this.ClientSize.Width - 200) * 10, (this.ClientSize.Height - 110) * 3));

            //학생 이름 입력 상자 위치 조정
            this.nameTextBox.Location = new Point((this.ClientSize.Width - 193) * 10, (this.ClientSize.Height - 3) * 3);

            //학번 입력 상자 위치 조정
            this.studentIdTextBox.Location = new Point((this.ClientSize.Width - 193) * 10, (this.ClientSize.Height + 20) * 3);

            // 좌석 예약 번호 입력 상자 위치 조정
            this.numTextBox.Location = new Point((this.ClientSize.Width - 193) * 10, (this.ClientSize.Height + 43) * 3);


            // 레이블 생성 및 추가
            Label nameLabel = CreateLabel("학생 이름", new Point((this.ClientSize.Width + 1315) / 2, (this.ClientSize.Height - 3) * 3));
            nameLabel.TextAlign = ContentAlignment.MiddleRight; // 텍스트 오른쪽 정렬
            nameLabel.BackColor = Color.White; // 배경색 설정
            this.Controls.Add(nameLabel);

            Label studentIdLabel = CreateLabel("학번", new Point((this.ClientSize.Width + 1315) / 2, (this.ClientSize.Height + 20) * 3));
            studentIdLabel.TextAlign = ContentAlignment.MiddleRight; // 텍스트 오른쪽 정렬
            studentIdLabel.BackColor = Color.White; // 배경색 설정
            this.Controls.Add(studentIdLabel);

            Label reservationNumberLabel = CreateLabel("좌석 예약 번호", new Point((this.ClientSize.Width + 1315) / 2, (this.ClientSize.Height + 43) * 3));
            reservationNumberLabel.TextAlign = ContentAlignment.MiddleRight; // 텍스트 오른쪽 정렬
            reservationNumberLabel.BackColor = Color.White; // 배경색 설정
            this.Controls.Add(reservationNumberLabel);



            // 폼에 새로운 입력 상자 추가
            this.Controls.Add(this.nameTextBox);
            this.Controls.Add(this.studentIdTextBox);


            // 확인 버튼 생성 및 설정
            submitButton = new Button
            {
                Text = "확인", // 버튼 텍스트
                Size = new Size(90, 90), // 버튼 크기
                Location = new Point((int)(this.ClientSize.Width * 4.25), (int)(this.ClientSize.Height * 3.1)) // 버튼 위치
            };
            submitButton.Click += SubmitButton_Click; // 버튼 클릭 시 이벤트 핸들러 추가

            // 폼에 입력 상자와 버튼 추가
            this.Controls.Add(this.numTextBox);
            this.Controls.Add(submitButton);

            connection = new MySqlConnection(connectionString); // MySQL 연결 객체 생성

            this.Load += LockScreenForm_Load; // 폼 로드 시 호출될 이벤트 핸들러 추가
            this.SizeChanged += LockScreenForm_SizeChanged; // 폼 크기 변경 시 호출될 이벤트 핸들러 추가

            // 포그라운드 창 확인 타이머 설정
            checkForegroundTimer = new Timer
            {
                Interval = 500 // 타이머 간격 설정 (500ms)
            };
            checkForegroundTimer.Tick += CheckForegroundTimer_Tick; // 타이머 틱 이벤트 핸들러 추가
            checkForegroundTimer.Start(); // 타이머 시작

            _hookID = SetHook(_proc); // 키보드 훅 설정
        }

        private void LockScreenForm_Load(object sender, EventArgs e)
        {
            // 폼 로드 시 마우스 제한 영역 업데이트
            UpdateMouseRestriction();
        }

        private void LockScreenForm_SizeChanged(object sender, EventArgs e)
        {
            // 폼 크기 변경 시 마우스 제한 영역 업데이트
            UpdateMouseRestriction();
        }

        private void UpdateMouseRestriction()
        {
            // 폼의 클라이언트 영역에 대한 마우스 제한 영역을 설정
            Rectangle rect = this.RectangleToScreen(this.ClientRectangle);
            Cursor.Clip = rect;
        }

        // 버튼 클릭 시 처리할 메서드
        private void SubmitForm()
        {
            string errorMessage = "";

            // 모든 필드가 입력되었는지 확인
            if (!AreAllFieldsFilled())
            {
                errorMessage = "모든 필드를 올바르게 입력해야 합니다.";
            }
            else
            {
                // 좌석 번호가 올바른지 확인
                bool isSeatNumCorrect = IsSeatNumCorrect();

                if (!isSeatNumCorrect)
                {
                    errorMessage = "이름 혹은 학번, 좌석 예약 번호가 올바르지 않습니다.";
                }
                else
                {
                    // 예약 번호가 확인되었을 때 처리
                    MessageBox.Show("좌석 예약 번호가 확인되었습니다.");
                    this.Close(); // 폼 닫기
                    return;
                }
            }

            // 에러 메시지 표시
            MessageBox.Show(errorMessage);
        }

        // 버튼 클릭 시 호출될 이벤트 핸들러
        private void SubmitButton_Click(object sender, EventArgs e)
        {
            SubmitForm();  // 버튼 클릭 시 SubmitForm 메서드 호출
        }

        private bool AreAllFieldsFilled()
        {
            return nameTextBox.Text != "학생 이름" &&
                   studentIdTextBox.Text != "학번" &&
                   numTextBox.Text != "좌석 예약 번호";
        }

        private bool IsSeatNumCorrect()
        {
            MySqlConnection localConnection = null;
            try
            {
                localConnection = new MySqlConnection(connectionString);
                localConnection.Open();

                string query = @"
                SELECT r.reservNum, y.name, y.studentId
                FROM Reservation r
                JOIN Yuhan y ON r.user_id = y.id
                WHERE r.classroom_name = @classroomName 
                AND r.reservSeat = @seat 
                AND r.reservNum = @reservNum
                AND y.name = @name
                AND y.studentId = @studentId;";

                using (MySqlCommand command = new MySqlCommand(query, localConnection))
                {
                    command.Parameters.AddWithValue("@classroomName", classroomName);
                    command.Parameters.AddWithValue("@seat", seat);
                    command.Parameters.AddWithValue("@reservNum", numTextBox.Text);
                    command.Parameters.AddWithValue("@name", nameTextBox.Text);
                    command.Parameters.AddWithValue("@studentId", studentIdTextBox.Text);

                    using (MySqlDataReader reader = command.ExecuteReader())
                    {
                        if (reader.Read())
                        {
                            string reservNum = reader["reservNum"].ToString();

                            // DataReader를 명시적으로 닫습니다.
                            reader.Close();

                            // 락스크린 잠금 해제
                            MessageBox.Show($"환영합니다, {nameTextBox.Text}님 (학번: {studentIdTextBox.Text})");

                            // 연결을 닫고 UpdateAttendance 메서드를 호출합니다.
                            localConnection.Close();
                            UpdateAttendance(reservNum);

                            return true;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("데이터베이스 연결에 실패했습니다: " + ex.Message);
            }
            finally
            {
                if (localConnection != null && localConnection.State == ConnectionState.Open)
                {
                    localConnection.Close();
                }
            }

            return false;
        }

        private void UpdateAttendance(string reservNum)
        {
            MySqlConnection localConnection = null;
            try
            {
                localConnection = new MySqlConnection(connectionString);
                localConnection.Open();

                // 1. 예약 정보 가져오기
                string reservationQuery = @"
            SELECT user_id, day, subject, classroom_name
            FROM Reservation
            WHERE reservNum = @reservNum;";

                string userId = "", day = "", subject = "", classroomName = "";

                using (MySqlCommand command = new MySqlCommand(reservationQuery, localConnection))
                {
                    command.Parameters.AddWithValue("@reservNum", reservNum);

                    using (MySqlDataReader reader = command.ExecuteReader())
                    {
                        if (reader.Read())
                        {
                            userId = reader["user_id"].ToString();
                            day = reader["day"].ToString();
                            subject = reader["subject"].ToString();
                            classroomName = reader["classroom_name"].ToString();
                        }
                        else
                        {
                            MessageBox.Show("해당 예약 번호의 정보를 찾을 수 없습니다.");
                            return;
                        }
                    }
                }

                // 2. 학생 시간표 확인 및 출석 처리
                string updateQuery = @"
            UPDATE StuTimetable 
            SET attendance = TRUE 
            WHERE user_id = @userId 
            AND day = @day 
            AND subject = @subject 
            AND classroomName = @classroomName
            AND attendance = FALSE;";

                using (MySqlCommand command = new MySqlCommand(updateQuery, localConnection))
                {
                    command.Parameters.AddWithValue("@userId", userId);
                    command.Parameters.AddWithValue("@day", day);
                    command.Parameters.AddWithValue("@subject", subject);
                    command.Parameters.AddWithValue("@classroomName", classroomName);

                    int rowsAffected = command.ExecuteNonQuery();
                    if (rowsAffected > 0)
                    {
                        MessageBox.Show("출석이 처리되었습니다.");
                    }
                    else
                    {
                        // 시간표 확인을 위한 SELECT 쿼리
                        string checkQuery = @"
                    SELECT * FROM StuTimetable 
                    WHERE user_id = @userId 
                    AND day = @day 
                    AND subject = @subject 
                    AND classroomName = @classroomName;";

                        using (MySqlCommand checkCommand = new MySqlCommand(checkQuery, localConnection))
                        {
                            checkCommand.Parameters.AddWithValue("@userId", userId);
                            checkCommand.Parameters.AddWithValue("@day", day);
                            checkCommand.Parameters.AddWithValue("@subject", subject);
                            checkCommand.Parameters.AddWithValue("@classroomName", classroomName);

                            using (MySqlDataReader reader = checkCommand.ExecuteReader())
                            {
                                if (reader.HasRows)
                                {
                                    MessageBox.Show("이미 출석 처리되었거나 시간표가 일치하지 않습니다.");
                                }
                                else
                                {
                                    MessageBox.Show("해당 시간표 정보를 찾을 수 없습니다.");
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"출석 처리 중 오류 발생: {ex.Message}");
                Console.WriteLine($"Stack Trace: {ex.StackTrace}");
                MessageBox.Show("출석 처리 중 오류가 발생했습니다: " + ex.Message);
            }
            finally
            {
                if (localConnection != null && localConnection.State == ConnectionState.Open)
                {
                    localConnection.Close();
                }
            }
        }

        private void CustomForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            // 폼이 사용자에 의해 닫힐 때 모든 필드가 입력되었는지 확인
            if (e.CloseReason == CloseReason.UserClosing && !AreAllFieldsFilled())
            {
                MessageBox.Show("모든 필드를 올바르게 입력해야 합니다.");
                e.Cancel = true; // 폼 닫기를 취소
            }
        }

        private void CheckForegroundTimer_Tick(object sender, EventArgs e)
        {
            IntPtr foregroundWindow = GetForegroundWindow(); // 현재 포그라운드 창 핸들 가져오기
            if (foregroundWindow != this.Handle)
            {
                // 포그라운드 창이 현재 폼이 아닌 경우, 폼을 최상위로 설정
                SetForegroundWindow(this.Handle);
                ShowWindow(this.Handle, SW_SHOWMAXIMIZED); // 폼 최대화
            }
        }

        private IntPtr SetHook(LowLevelKeyboardProc proc)
        {
            using (Process curProcess = Process.GetCurrentProcess())
            using (ProcessModule curModule = curProcess.MainModule)
            {
                // 현재 프로세스의 모듈 핸들을 사용하여 훅을 설정
                return SetWindowsHookEx(WH_KEYBOARD_LL, proc, GetModuleHandle(curModule.ModuleName), 0);
            }
        }

        private IntPtr HookCallback(int nCode, IntPtr wParam, IntPtr lParam)
        {
            if (nCode >= 0 && (wParam == (IntPtr)WM_KEYDOWN || wParam == (IntPtr)WM_SYSKEYDOWN))
            {
                // 키가 눌렸을 때 처리
                int vkCode = Marshal.ReadInt32(lParam); // 가상 키 코드 읽기
                if (vkCode == (int)Keys.Escape)
                {
                    // Esc 키를 비활성화
                    MessageBox.Show("Esc 키는 사용할 수 없습니다.");
                    return (IntPtr)1; // 메시지 처리를 방지하기 위해 1 반환
                }
            }
            return CallNextHookEx(_hookID, nCode, wParam, lParam); // 다음 훅 처리기로 메시지 전달
        }

        protected override void OnFormClosing(FormClosingEventArgs e)
        {
            // 폼이 닫힐 때 마우스 클리핑 해제
            Cursor.Clip = Rectangle.Empty;
            base.OnFormClosing(e);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing && (_hookID != IntPtr.Zero))
            {
                UnhookWindowsHookEx(_hookID); // 훅 해제
            }
            base.Dispose(disposing);
        }

        private TextBox CreatePlaceholderTextBox(string placeholder, Point location)
        {
            TextBox textBox = new TextBox
            {
                Text = placeholder, // 플레이스홀더 텍스트 설정
                ForeColor = Color.Gray, // 텍스트 색상 설정
                Location = location, // 위치 설정
                Size = new Size(250, 30) // 크기 설정
            };

            // 플레이스홀더 텍스트 처리
            textBox.Enter += (sender, e) =>
            {
                if (textBox.Text == placeholder)
                {
                    textBox.Text = ""; // 플레이스홀더 제거
                    textBox.ForeColor = Color.Black; // 텍스트 색상 변경
                }
            };

            textBox.Leave += (sender, e) =>
            {
                if (textBox.Text == "")
                {
                    textBox.Text = placeholder; // 플레이스홀더 복원
                    textBox.ForeColor = Color.Gray; // 텍스트 색상 변경
                }
            };

            return textBox;
        }

        private Label CreateLabel(string text, Point location)
        {
            return new Label
            {
                Text = text, // 레이블 텍스트 설정
                Location = location, // 위치 설정
                Size = new Size(90, 30), // 크기 설정
                TextAlign = ContentAlignment.MiddleLeft // 텍스트 정렬 설정
            };
        }
    }
}
