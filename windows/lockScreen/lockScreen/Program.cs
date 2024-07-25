using System;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;
using MySql.Data.MySqlClient;

namespace lockScreen
{
    internal static class Program
    {
        [STAThread]
        static void Main()
        {
            // 시각적인 스타일을 활성화하고 텍스트 렌더링을 기본값으로 설정
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            // CustomForm을 실행하여 애플리케이션 실행.
            Application.Run(new LockScreenForm());
        }
    }

    public class LockScreenForm : Form
    {
        private TextBox passwordTextBox;
        private MySqlConnection connection;

        public LockScreenForm()
        {
            // 폼의 제목을 설정
            this.Text = "LOCK SCREEN";
            // 폼의 테두리 스타일을 없애고 창이 최대화된 상태로 시작하도록 설정
            this.FormBorderStyle = FormBorderStyle.None;
            this.WindowState = FormWindowState.Maximized;
            // 폼이 닫힐 때 CustomForm_FormClosing 메서드를 호출
            this.FormClosing += CustomForm_FormClosing;
            // 폼이 다른 창 위에 항상 표시되도록 설정합니다. alt+tab 눌러서 다른 창으로 넘어가지는 것 방지.
            this.TopMost = true;

            // 배경 이미지를 설정
            string imagePath = @"..\..\..\Images\yuhan.png";
            this.BackgroundImage = Image.FromFile(imagePath);
            // 배경 이미지가 폼을 채우도록 설정
            this.BackgroundImageLayout = ImageLayout.Stretch;

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
            /// MySQL 서버와의 연결을 설정하기 위한 연결 문자열을 생성
            //  로컬 호스트의 MySQL 서버에 'YuhanDB'라는 데이터베이스에 'root' 사용자로 연결
            // 'pwd' = 'root' 비밀번호를 사용하도록 설정했습니다.
            string connectionString = "server=localhost;database=YuhanDB;uid=root;pwd=root;";

            // MySqlConnection 객체를 생성하여 MySQL 서버에 연결합니다.
            // 연결 문자열을 사용하여 MySQL 서버와의 연결을 설정합니다.
            connection = new MySqlConnection(connectionString);
        }

        // 비밀번호 입력 상자 확인하는 핸들러
        private void PasswordTextBox_KeyDown(object sender, KeyEventArgs e)
        {
            // 엔터 키가 눌렸는지 확인
            if (e.KeyCode == Keys.Enter)
            {
                // 입력된 비밀번호가 올바른지 확인
                if (IsPasswordCorrect())
                {
                    // 올바른 비밀번호일 경우 메시지를 표시하고 프로그램을 종료
                    MessageBox.Show("비밀번호가 확인되었습니다.");
                    this.Close();
                }
                else
                {
                    // 잘못된 비밀번호일 경우 메시지를 표시하고 입력 내용을 초기화
                    MessageBox.Show("비밀번호가 올바르지 않습니다.");
                    this.passwordTextBox.Clear();
                }
            }
        }

        // 데이터베이스에서 비밀번호를 가져와 입력된 비밀번호와 비교
        private bool IsPasswordCorrect()
        {
            try
            {
                // 데이터베이스 연결
                connection.Open();

                // 데이터베이스 쿼리를 작성합니다.
                string query = "SELECT num FROM Yuhan WHERE id = 'kjunh972';";
                // 데이터베이스 연결을 사용하여 쿼리를 실행할 MySqlCommand 객체를 생성
                MySqlCommand command = new MySqlCommand(query, connection);
                // ExecuteScalar 메서드를 사용하여 단일 값을 가져옴
                // 여기서는 'num' 필드의 값을 가져오기 위해 사용
                object result = command.ExecuteScalar();
                // 데이터베이스 연결 종료
                connection.Close();

                // 비밀번호 확인
                return result != null && result.ToString() == passwordTextBox.Text;
            }
            catch (Exception ex)
            {
                // 데이터베이스 연결에 실패한 경우 예외 메시지 출력
                MessageBox.Show("데이터베이스 연결에 실패했습니다: " + ex.Message);
                return false; // 연결 실패로 처리
            }
        }


        // 폼의 배경을 불투명
        protected override void OnPaint(PaintEventArgs e)
        {
            base.OnPaint(e);

            // 하얀색 브러쉬를 사용해 불투명하게 만들기
            using (SolidBrush brush = new SolidBrush(Color.FromArgb(170, Color.White)))
            {
                Rectangle squareOp = new Rectangle(645, 400, this.ClientSize.Width / 4, 200);
                e.Graphics.FillRectangle(brush, squareOp);
            }
        }

        // alt + f4 로 인한 종료 방지
        private void CustomForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            // alt + f4 방지하고 올바른 비밀번호 입력하면 종료되게 하기
            if (e.CloseReason == CloseReason.UserClosing && !IsPasswordCorrect())
            {
                e.Cancel = true;
            }
        }
    }
}