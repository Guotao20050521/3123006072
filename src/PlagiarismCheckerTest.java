import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * PlagiarismChecker的单元测试类
 */
public class PlagiarismCheckerTest {

    public static void main(String[] args) {
        testPreprocessText();
        testCompareTexts();
        testFileOperations();
        System.out.println("所有测试完成");
    }

    /**
     * 测试文本预处理功能
     */
    private static void testPreprocessText() {
        System.out.println("开始测试文本预处理功能...");

        // 测试用例1：包含标点符号和空格的文本
        String text1 = "今天是星期天，天气晴，今天晚上我要去看电影。";
        String expected1 = "今天是星期天天气晴今天晚上我要去看电影";
        String result1 = preprocessTextForTest(text1);
        assert result1.equals(expected1) : "测试用例1失败: 期望 '" + expected1 + "', 实际 '" + result1 + "'";
        System.out.println("测试用例1通过");

        // 测试用例2：包含英文和数字的文本
        String text2 = "Hello, World! 123";
        String expected2 = "helloworld123";
        String result2 = preprocessTextForTest(text2);
        assert result2.equals(expected2) : "测试用例2失败: 期望 '" + expected2 + "', 实际 '" + result2 + "'";
        System.out.println("测试用例2通过");

        System.out.println("文本预处理功能测试通过\n");
    }

    /**
     * 测试文本比较功能
     */
    private static void testCompareTexts() {
        System.out.println("开始测试文本比较功能...");

        // 测试用例1：完全相同的文本
        String text1 = "今天是星期天天气晴";
        String text2 = "今天是星期天天气晴";
        double similarity1 = PlagiarismChecker.compareTexts(text1, text2);
        assert Math.abs(similarity1 - 1.0) < 0.01 : "测试用例1失败: 期望 1.0, 实际 " + similarity1;
        System.out.println("测试用例1通过：完全相同文本相似度为 " + String.format("%.2f", similarity1));

        // 测试用例2：完全不同的文本
        String text3 = "今天是星期天";
        String text4 = "明天是星期一";
        double similarity2 = PlagiarismChecker.compareTexts(text3, text4);
        assert similarity2 >= 0 : "测试用例2失败: 相似度应该大于等于0, 实际 " + similarity2;
        System.out.println("测试用例2通过：不同文本相似度为 " + String.format("%.2f", similarity2));

        // 测试用例3：部分相同的文本
        String text5 = "今天是星期天天气晴";
        String text6 = "今天是周天天气晴朗";
        double similarity3 = PlagiarismChecker.compareTexts(text5, text6);
        assert similarity3 > 0 && similarity3 < 1 : "测试用例3失败: 相似度应该在0-1之间, 实际 " + similarity3;
        System.out.println("测试用例3通过：部分相同文本相似度为 " + String.format("%.2f", similarity3));

        System.out.println("文本比较功能测试通过\n");
    }

    /**
     * 测试文件操作功能
     */
    private static void testFileOperations() {
        System.out.println("开始测试文件操作功能...");

        try {
            // 创建测试文件
            String originalFile = "original_test.txt";
            String plagiarizedFile = "plagiarized_test.txt";
            String answerFile = "answer_test.txt";

            // 写入测试内容
            writeTestFile(originalFile, "今天是星期天，天气晴，今天晚上我要去看电影。");
            writeTestFile(plagiarizedFile, "今天是周天，天气晴朗，我晚上要去看电影。");

            // 测试查重功能
            double similarity = PlagiarismChecker.checkPlagiarism(originalFile, plagiarizedFile);
            assert similarity >= 0 && similarity <= 1 : "文件操作测试失败: 相似度应该在0-1之间, 实际 " + similarity;

            // 测试结果写入功能
            PlagiarismChecker.writeResult(similarity, answerFile);

            // 验证结果文件内容
            String result = readTestFile(answerFile);
            assert result.matches("\\d+\\.\\d{2}") : "结果文件格式错误: " + result;

            // 清理测试文件
            new File(originalFile).delete();
            new File(plagiarizedFile).delete();
            new File(answerFile).delete();

            System.out.println("文件操作功能测试通过，结果文件内容: " + result + "\n");
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "文件操作测试异常: " + e.getMessage();
        }
    }

    /**
     * 用于测试的文本预处理方法
     */
    private static String preprocessTextForTest(String text) {
        return text.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9]", "").toLowerCase();
    }

    /**
     * 写入测试文件
     */
    private static void writeTestFile(String fileName, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            writer.write(content);
        }
    }

    /**
     * 读取测试文件
     */
    private static String readTestFile(String fileName) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }
}