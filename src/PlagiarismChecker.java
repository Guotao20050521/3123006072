import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 论文查重算法实现类
 * 该类用于比较两个文本文件的相似度，计算重复率
 */
public class PlagiarismChecker
{
    /**
     * 主函数，处理命令行参数并执行查重
     * @param args 命令行参数：原文文件路径、抄袭版文件路径、答案文件路径
     */
    public static void main(String[] args) {
        /*检查参数数量*/
        if (args.length != 3) {
            System.err.println("参数数量错误，请提供原文文件、抄袭版文件和答案文件的路径");
            return;
        }

        String originalFilePath = args[0];
        String plagiarizedFilePath = args[1];
        String answerFilePath = args[2];

        try {
            double similarity = checkPlagiarism(originalFilePath, plagiarizedFilePath);  //执行查重并获取重复率
            writeResult(similarity, answerFilePath);  //将结果写入答案文件
            System.out.println("查重完成，重复率为: " + String.format("%.2f", similarity));
        } catch (Exception e) {
            System.err.println("执行过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 计算两个文件的相似度
     * @param originalFilePath 原文文件路径
     * @param plagiarizedFilePath 抄袭版文件路径
     * @return 相似度（0-1之间的浮点数）
     * @throws IOException 文件读取异常
     */
    public static double checkPlagiarism(String originalFilePath, String plagiarizedFilePath) throws IOException {
        // 读取文件内容
        String originalText = readFile(originalFilePath);
        String plagiarizedText = readFile(plagiarizedFilePath);

        // 文本预处理：去除标点符号，转换为小写等
        String processedOriginal = preprocessText(originalText);
        String processedPlagiarized = preprocessText(plagiarizedText);

        // 计算相似度
        return calculateSimilarity(processedOriginal, processedPlagiarized);
    }

    /**
     * 读取文件内容
     * @param filePath 文件路径
     * @return 文件内容字符串
     * @throws IOException 文件读取异常
     */
    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    /**
     * 文本预处理，去除标点符号和空格，统一格式
     * @param text 原始文本
     * @return 处理后的文本
     */
    private static String preprocessText(String text) {
        return text.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9]", "").toLowerCase();
    }

    /**
     * 计算两个文本的相似度
     * 使用余弦相似度算法
     * @param text1 文本1
     * @param text2 文本2
     * @return 相似度（0-1之间的浮点数）
     */
    private static double calculateSimilarity(String text1, String text2) {
        /*如果两个文本都为空，则相似度为1*/
        if (text1.isEmpty() && text2.isEmpty()) {
            return 1.0;
        }

        /*如果其中一个文本为空，则相似度为0*/
        if (text1.isEmpty() || text2.isEmpty()) {
            return 0.0;
        }

        /*使用简单的基于字符的相似度计算*/
        return calculateCharBasedSimilarity(text1, text2);
    }

    /**
     * 基于字符的相似度计算
     * @param text1 文本1
     * @param text2 文本2
     * @return 相似度（0-1之间的浮点数）
     */
    private static double calculateCharBasedSimilarity(String text1, String text2) {
        /*将文本转换为字符数组*/
        Set<Character> set1 = new HashSet<>();
        Set<Character> set2 = new HashSet<>();
        for (char c : text1.toCharArray()) {
            set1.add(c);
        }
        for (char c : text2.toCharArray()) {
            set2.add(c);
        }

        Set<Character> intersection = new HashSet<>(set1);  //交集
        intersection.retainAll(set2);
        Set<Character> union = new HashSet<>(set1);  //并集
        union.addAll(set2);

        /*如果并集为空，说明两个集合都为空，相似度为1*/
        if (union.isEmpty()) {
            return 1.0;
        }
        return (double) intersection.size() / union.size();  //返回交集与并集的比值（Jaccard相似度）
    }

    /**
     * 将结果写入答案文件
     * @param similarity 相似度
     * @param answerFilePath 答案文件路径
     * @throws IOException 文件写入异常
     */
    private static void writeResult(double similarity, String answerFilePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(answerFilePath), StandardCharsets.UTF_8))) {
            writer.write(String.format("%.2f", similarity));
        }
    }

    /**
     * 用于单元测试的辅助方法，比较两个字符串的相似度
     * @param text1 文本1
     * @param text2 文本2
     * @return 相似度（0-1之间的浮点数）
     */
    public static double compareTexts(String text1, String text2) {
        String processedText1 = preprocessText(text1);
        String processedText2 = preprocessText(text2);
        return calculateSimilarity(processedText1, processedText2);
    }
}