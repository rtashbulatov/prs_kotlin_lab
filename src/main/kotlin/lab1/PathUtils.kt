package lab1

fun getPathDependsOnOs(): String {
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.contains("win") -> "C:\\Users\\PC\\IdeaProjects\\prs_kotlin_lab\\src\\main\\kotlin\\lab1\\lab1.txt"
        os.contains("nix") || os.contains("nux") -> "/home/rtashbulatov/IdeaProjects/utmn/src/main/kotlin/lab1/lab1.txt"
        else -> ""
    }
}