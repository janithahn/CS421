import load.LoadData;

public class App {
    public static void main(String[] args) {
        LoadData loadData = new LoadData("./datasets/adult.csv");
        //System.out.println(loadData.getOriginalDataList(4));
        loadData.printStructure();
    }
}
