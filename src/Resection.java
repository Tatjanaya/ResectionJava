import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.border.Border;

public class Resection {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ShowFrame showFrame = new ShowFrame();
                showFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                showFrame.setTitle("单片空间后方交会");
                showFrame.setVisible(true);
            }
        });
    }
}

class ShowFrame extends JFrame {
    private static final int WIDTH=900;
    private static final int HEIGHT=600;
    public ShowFrame() {
        // 设置大小
        setSize(WIDTH, HEIGHT);

        // 设置其出现的位置，即把它显示在屏幕正中间
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        setLocation(screenSize.width / 2 - WIDTH / 2, screenSize.height / 2 - HEIGHT / 2);
        add(new ShowPanel());
    }
}

class ShowPanel extends JPanel {
    private JButton button1; // 确定运行键
    private JButton button2; // 退出键
    private JButton button3; // 打开文件对话框
    private JTextField textField1; // 文件打开路径
    private JTextArea textArea1; // 成果输出区域
    private JFileChooser fileChooser; // 文件对话框
    private JScrollPane jScrollPane; // 滑动条

    public ShowPanel() {
        // 设置为绝对布局
        this.setLayout(null);
        // 设置面板颜色
        this.setBackground(Color.WHITE);
        // 初始化控件
        button1 = new JButton("确定");
        button2 = new JButton("退出");
        button3 = new JButton("路径");
        textField1 = new JTextField();
        textArea1 = new JTextArea();
        fileChooser = new JFileChooser("D:\\pra");
        jScrollPane = new JScrollPane(textArea1);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // 设置字体
        Font font = new Font("宋体", Font.BOLD, 20);
        button1.setFont(font);
        button2.setFont(font);
        button3.setFont(font);

        // 将控件添加到面板上
        add(button1);
        add(button2);
        add(button3);
        add(textField1);
        //add(textArea1);
        add(jScrollPane);

        // 设置控件位置
        button1.setBounds(100, 450, 100, 50);
        button2.setBounds(700, 450, 100, 50);
        button3.setBounds(700, 50, 100, 25);
        textField1.setBounds(100, 50, 600, 25);
        textArea1.setBounds(100, 100, 700, 300);
        jScrollPane.setBounds(100, 100, 700, 300);

        // 添加按钮的监听器
        button2.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });

        button3.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int returnVal = fileChooser.showOpenDialog(fileChooser);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String filepath = fileChooser.getSelectedFile().getAbsolutePath();
                    textField1.setText(filepath);
                }
            }
        });

        button1.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String filepath = textField1.getText();
                ArrayList<Double> datas = new ArrayList<>();
                File file = new File(filepath);
                if (!file.exists()) {
                    textArea1.setText("can not open the file!");
                }
                try {
                    Scanner sc = new Scanner(file);
                    while (sc.hasNext()) {
                        datas.add(sc.nextDouble());
                    }
                    double m = 1.0 / 15000.0; // 影像比例尺
                    double f = datas.get(20) / 1000; // 摄影机主距
                    double x0 = datas.get(21), y0 = datas.get(22); // 内方位元素
                    double[][] x = new double[4][2];
                    double[][] xt = new double[4][2];
                    double[][] X = new double[4][3];
                    int ii = 0, jj = 8;
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 2; j++) {
                            xt[i][j] = datas.get(ii);
                            x[i][j] = datas.get(ii) / 1000;
                            ii++;
                        }
                    }
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 3; j++) {
                            X[i][j] = datas.get(jj);
                            jj++;
                        }
                    }
                    //System.out.println(x[1][1]);
                    int num = 0;
                    double[] X0 = new double[6];
                    double[][] R = new double[3][3];
                    double[] app = new double[8];
                    double[][] A = new double[8][6];
                    double[][] AT = new double[6][8];
                    double[] L = new double[8];
                    double[][] sumA = new double[6][6];
                    double[][] r1 = new double[6][8];
                    double[] r2 = new double[6];
                    double[] sumXYZ = new double[2];
                    double[][] invA = new double[6][6];
                    double[] rv = new double[8];
                    double rf = 0.0;
                    double[] s = new double[6];
                    textArea1.setText("已知像点坐标为： \n");
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 2; j++) {
                            if (j == 0) {
                                textArea1.append("x" + String.valueOf(i + 1) + "= " + String.valueOf(xt[i][j]) + "  ");
                            }
                            else {
                                textArea1.append("y" + String.valueOf(i + 1) + "= " + String.valueOf(xt[i][j]) + "\n");
                            }
                        }
                    }
                    textArea1.append("已知地面四个点的坐标为： \n");
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (j == 0) {
                                textArea1.append("X" + String.valueOf(i + 1) + "= " + String.valueOf(X[i][j]) + "  ");
                            }
                            else if (j == 1) {
                                textArea1.append("Y" + String.valueOf(i + 1) + "= " + String.valueOf(X[i][j]) + "  ");
                            }
                            else {
                                textArea1.append("Z" + String.valueOf(i + 1) + "= " + String.valueOf(X[i][j]) + "\n");
                            }
                        }
                    }
                    for (int j = 0; j < 2; j++) {
                        for (int i = 0; i < 4; i++) {
                            sumXYZ[j] += X[i][j];
                        }
                    }
                    for (int i = 0; i < 2; i++) {
                        X0[i] = sumXYZ[i] / 4;
                    }
                    X0[2] = 2298.6;
                    /*for (int i = 0; i < 6; i++) {
                        System.out.println(X0[i]);
                    }*/
                    do {
                        R[0][0] = Math.cos(X0[3]) * Math.cos(X0[5]) - Math.sin(X0[3]) * Math.sin(X0[4]) * Math.sin(X0[5]);
                        R[0][1] = -Math.cos(X0[3]) * Math.sin(X0[5]) - Math.sin(X0[3]) * Math.sin(X0[4]) * Math.cos(X0[5]);
                        R[0][2] = -Math.sin(X0[3]) * Math.cos(X0[4]);
                        R[1][0] = Math.cos(X0[4]) * Math.sin(X0[5]);
                        R[1][1] = Math.cos(X0[4]) * Math.cos(X0[5]);
                        R[1][2] = -Math.sin(X0[4]);
                        R[2][0] = Math.sin(X0[3]) * Math.cos(X0[5]) + Math.cos(X0[3]) * Math.sin(X0[4]) * Math.sin(X0[5]);
                        R[2][1] = -Math.sin(X0[3]) * Math.sin(X0[5]) + Math.cos(X0[3]) * Math.sin(X0[4]) * Math.cos(X0[5]);
                        R[2][2] = Math.cos(X0[3]) * Math.cos(X0[4]);

                        app[0] = -f * (R[0][0] * (X[0][0] - X0[0]) + R[1][0] * (X[0][1] - X0[1]) + R[2][0] * (X[0][2] - X0[2])) / (R[0][2] * (X[0][0] - X0[0]) + R[1][2] * (X[0][1] - X0[1]) + R[2][2] * (X[0][2] - X0[2]));
                        app[1] = -f * (R[0][1] * (X[0][0] - X0[0]) + R[1][1] * (X[0][1] - X0[1]) + R[2][1] * (X[0][2] - X0[2])) / (R[0][2] * (X[0][0] - X0[0]) + R[1][2] * (X[0][1] - X0[1]) + R[2][2] * (X[0][2] - X0[2]));
                        app[2] = -f * (R[0][0] * (X[1][0] - X0[0]) + R[1][0] * (X[1][1] - X0[1]) + R[2][0] * (X[1][2] - X0[2])) / (R[0][2] * (X[1][0] - X0[0]) + R[1][2] * (X[1][1] - X0[1]) + R[2][2] * (X[1][2] - X0[2]));
                        app[3] = -f * (R[0][1] * (X[1][0] - X0[0]) + R[1][1] * (X[1][1] - X0[1]) + R[2][1] * (X[1][2] - X0[2])) / (R[0][2] * (X[1][0] - X0[0]) + R[1][2] * (X[1][1] - X0[1]) + R[2][2] * (X[1][2] - X0[2]));
                        app[4] = -f * (R[0][0] * (X[2][0] - X0[0]) + R[1][0] * (X[2][1] - X0[1]) + R[2][0] * (X[2][2] - X0[2])) / (R[0][2] * (X[2][0] - X0[0]) + R[1][2] * (X[2][1] - X0[1]) + R[2][2] * (X[2][2] - X0[2]));
                        app[5] = -f * (R[0][1] * (X[2][0] - X0[0]) + R[1][1] * (X[2][1] - X0[1]) + R[2][1] * (X[2][2] - X0[2])) / (R[0][2] * (X[2][0] - X0[0]) + R[1][2] * (X[2][1] - X0[1]) + R[2][2] * (X[2][2] - X0[2]));
                        app[6] = -f * (R[0][0] * (X[3][0] - X0[0]) + R[1][0] * (X[3][1] - X0[1]) + R[2][0] * (X[3][2] - X0[2])) / (R[0][2] * (X[3][0] - X0[0]) + R[1][2] * (X[3][1] - X0[1]) + R[2][2] * (X[3][2] - X0[2]));
                        app[7] = -f * (R[0][1] * (X[3][0] - X0[0]) + R[1][1] * (X[3][1] - X0[1]) + R[2][1] * (X[3][2] - X0[2])) / (R[0][2] * (X[3][0] - X0[0]) + R[1][2] * (X[3][1] - X0[1]) + R[2][2] * (X[3][2] - X0[2]));

                        for (int i = 0; i < 4; i++) {
                            /*a11*/A[2 * i][0] = (R[0][0] * f + R[0][2] * app[2 * i]) / (R[0][2] * (X[i][0] - X0[0]) + R[1][2] * (X[i][1] - X0[1]) + R[2][2] * (X[i][2] - X0[2]));
                            /*a12*/A[2 * i][1] = (R[1][0] * f + R[1][2] * app[2 * i]) / (R[0][2] * (X[i][0] - X0[0]) + R[1][2] * (X[i][1] - X0[1]) + R[2][2] * (X[i][2] - X0[2]));
                            /*a13*/A[2 * i][2] = (R[2][0] * f + R[2][2] * app[2 * i]) / (R[0][2] * (X[i][0] - X0[0]) + R[1][2] * (X[i][1] - X0[1]) + R[2][2] * (X[i][2] - X0[2]));
                            /*a21*/A[2 * i + 1][0] = (R[0][1] * f + R[0][2] * app[2 * i + 1]) / (R[0][2] * (X[i][0] - X0[0]) + R[1][2] * (X[i][1] - X0[1]) + R[2][2] * (X[i][2] - X0[2]));
                            /*a22*/A[2 * i + 1][1] = (R[1][1] * f + R[1][2] * app[2 * i + 1]) / (R[0][2] * (X[i][0] - X0[0]) + R[1][2] * (X[i][1] - X0[1]) + R[2][2] * (X[i][2] - X0[2]));
                            /*a23*/A[2 * i + 1][2] = (R[2][1] * f + R[2][2] * app[2 * i + 1]) / (R[0][2] * (X[i][0] - X0[0]) + R[1][2] * (X[i][1] - X0[1]) + R[2][2] * (X[i][2] - X0[2]));
                            /*a14*/A[2 * i][3] = app[2 * i + 1] * Math.sin(X0[4]) - (app[2 * i] / f * (app[2 * i] * Math.cos(X0[5]) - app[2 * i + 1] * Math.sin(X0[5])) + f * Math.cos(X0[5])) * Math.cos(X0[4]);
                            /*a15*/A[2 * i][4] = -f * Math.sin(X0[5]) - app[2 * i] / f * (app[2 * i] * Math.sin(X0[5]) + app[2 * i + 1] * Math.cos(X0[5]));
                            /*a16*/A[2 * i][5] = app[2 * i + 1];
                            /*a24*/A[2 * i + 1][3] = -1 * app[2 * i] * Math.sin(X0[4]) - (app[2 * i + 1] / f * (app[2 * i] * Math.cos(X0[5]) - app[2 * i + 1] * Math.sin(X0[5])) - f * Math.sin(X0[5])) * Math.cos(X0[4]);
                            /*a25*/A[2 * i + 1][4] = -1 * f * Math.cos(X0[5]) - app[2 * i + 1] / f * (app[2 * i] * Math.sin(X0[5]) + app[2 * i + 1] * Math.cos(X0[5]));
                            /*a26*/A[2 * i + 1][5] = -app[2 * i];
                        }
                        /*for (int i = 0; i < 8; i++) {
                            for (int j = 0; j < 6; j++) {
                                System.out.println(A[i][j]);
                            }
                        }*/
                        // 进行常数项的初始化
                        for (int i = 0; i < 4; i++) {
                            L[2 * i] = x[i][0] - app[2 * i];
                            L[2 * i + 1] = x[i][1] - app[2 * i + 1];
                        }
                        /*for (int i = 0; i < 8; i++) {
                            System.out.println(L[i]);
                        }*/
                        // A的转置矩阵
                        for (int i = 0; i < 8; i++) {
                            for (int j = 0; j < 6; j++) {
                                AT[j][i] = A[i][j];
                                //System.out.println(AT[j][i]);
                            }
                        }

                        // sumA = AT * A
                        for (int i = 0; i < 6; i++) {
                            for (int j = 0; j < 6; j++) {
                                sumA[i][j] = 0;
                            }
                        }
                        for (int i = 0; i < 6; i++) {
                            for (int k = 0; k < 6; k++) {
                                for (int j = 0; j < 8; j++) {
                                    sumA[i][k] += AT[i][j] * A[j][k];
                                }
                            }
                        }

                        // sumA求逆，invA = (AT * A)^-1
                        Inverse(sumA, 6, invA);
                        for (int i = 0; i < 6; i++) {
                            for (int j = 0; j < 6; j++) {
                                System.out.println(sumA[i][j]);
                            }
                        }
                        //System.out.println(AT[1][2]);
                        // 实现矩阵sumA[6][6]与AT[6][8]的相乘，结果存放在r1[6][8]中
                        for (int i = 0; i < 6; i++) {
                            for (int j = 0; j < 8; j++) {
                                r1[i][j] = 0;
                            }
                        }
                        for (int i = 0; i < 6; i++) {
                            for (int k = 0; k < 8; k++) {
                                for (int j = 0; j < 6; j++) {
                                    r1[i][k] += invA[i][j] * AT[j][k];
                                    //System.out.println(r1[i][k]);
                                }
                            }
                        }
                        //System.out.println(L[1]);
                        // 实现r1[6][8]与L[8]的相乘，得到结果放在r2[6]中
                        for (int i = 0; i < 6; i++) {
                            r2[i] = 0;
                        }
                        //System.out.println(r2[1]);
                        for (int i = 0; i  < 6; i++) {
                            for (int j = 0; j < 8; j++) {
                                r2[i] += r1[i][j] * L[j];
                            }
                        }
                        //System.out.println(r2[1]);
                        num++;
                        for (int i = 0; i < 6; i++) {
                            X0[i] = X0[i] + r2[i];
                        }
                        //System.out.println(r2[1]);
                        textArea1.append("\n" + "第" + String.valueOf(num) + "次迭代：" + "\t" + "Xs" + "\t" + "Ys" + "\t" + "Zs" + "\t" + "ρ" + "\t" + "ω" + "\t" + "κ" + "\n");
                        textArea1.append("\n" + "第" + String.valueOf(num) + "次迭代：" + "\t" + String.format("%.5f", r2[0]) + "\t" + String.format("%.5f", r2[1]) + "\t" + String.format("%.5f", r2[2]) + "\t" + String.format("%.5f", r2[3]) + "\t" + String.format("%.5f", r2[4]) + "\t" + String.format("%.5f", r2[5]) + "\n");

                    } while (Math.abs(r2[3] * 206265.0) > 6 || Math.abs(r2[4] * 206265.0) > 6 || Math.abs(r2[5] * 206265.0) > 6);
                    textArea1.append("旋转矩阵R：\n");
                    textArea1.append(String.valueOf(R[0][0]) + "\t" + String.valueOf(R[0][1]) + "\t" + String.valueOf(R[0][2]) + "\n" +
                                     String.valueOf(R[1][0]) + "\t" + String.valueOf(R[1][1]) + "\t" + String.valueOf(R[1][2]) + "\n" +
                                     String.valueOf(R[2][0]) + "\t" + String.valueOf(R[2][1]) + "\t" + String.valueOf(R[2][2]) + "\n");
                    textArea1.append("\n\n限差条件满足，循环结束\n");
                    textArea1.append("Xs" + "\t" + "Ys" + "\t" + "Zs" + "\t" + "ρ" + "\t" + "ω" + "\t" + "κ" + "\n");
                    for (int i = 0; i < 6; i++) {
                        textArea1.append(String.format("%.5f", X0[i]) + "\t");
                    } // 外方位元素六个值

                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 6; j++) {
                            rv[i] += A[i][j] * r2[j];
                        }
                    }

                    for (int i = 0; i < 6; i++) {
                        rv[i] -= L[i];
                    }

                    for (int i = 0; i < 8; i++) {
                        rf += (rv[i] * rv[i]);
                    }

                    rf = Math.sqrt(rf / 2);
                    for (int i = 0; i < 6; i++) {
                        s[i] = Math.sqrt(invA[i][i]) * rf;
                    }
                    textArea1.append("\n单位权中误差-->" + String.valueOf(rf) + "\n");
                    textArea1.append("\n各个未知数中误差\n");
                    textArea1.append("Xs" + "\t" + "Ys" + "\t" + "Zs" + "\t" + "ρ" + "\t" + "ω" + "\t" + "κ" + "\n");
                    textArea1.append(String.format("%.5f", s[0]) + "\t" + String.format("%.5f", s[1]) + "\t" + String.format("%.5f", s[2]) + "\t" + String.format("%.5f", s[3]) + "\t" + String.format("%.5f", s[4]) + "\t" + String.format("%.5f", s[5]) + "\n");
                    
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    double getA(double[][] arcs, int n) {
        //arcs = new double[6][6];
        if (n == 1)
            return arcs[0][0];
        double ans = 0;
        double[][] temp = new double[6][6];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - 1; j++) {
                for (int k = 0; k < n - 1; k++) {
                    temp[j][k] = arcs[j + 1][(k >= i) ? k + 1: k];
                }
            }
            double t = getA(temp, n - 1);
            if (i % 2 == 0)
                ans += arcs[0][i] * t;
            else
                ans -= arcs[0][i] * t;
        }
        return ans;
    }

    void Algebra(double[][] arcs, int n, double[][] ans) {
        //arcs = new double[6][6];
        //ans = new double[6][6];
        if (n == 1) {
            ans[0][0] = 1;
        }
        double[][] temp = new double[6][6];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n - 1; k++) {
                    for (int t = 0; t < n - 1; t++) {
                        temp[k][t] = arcs[k >= i? k + 1: k][t >= j? t + 1: t];
                    }
                }
                ans[j][i] = getA(temp, n - 1);
                if ((i + j) % 2 == 1) {
                    ans[j][i] = -ans[j][i];
                }
            }
        }
    }

    void Inverse(double[][] src, int n, double[][] des) {
        //src = new double[6][6];
        //des = new double[6][6];
        double flag = getA(src, n);
        double[][] t = new double[6][6];
        Algebra(src, n, t);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                des[i][j] = t[i][j] / flag;
            }
        }
    }
}

