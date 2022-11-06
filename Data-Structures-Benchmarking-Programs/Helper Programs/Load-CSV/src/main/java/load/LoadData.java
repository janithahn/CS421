package load;

import tech.tablesaw.api.Table;

import java.util.List;

public class LoadData {
    private Table table;

    public LoadData(String filepath) {
        try {
            table  = Table.read().csv(filepath);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void loadData(String filename) {
        try {
            table  = Table.read().csv("./datasets/" + filename);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public Table getTable() {
        tableIsNull();
        return this.table;
    }

    public List<?> getOriginalDataList(int column) {
        tableIsNull();
        return this.table.column(column).asList();
    }

    public List<?> getOriginalDataList(String column) {
        tableIsNull();
        return this.table.column(column).asList();
    }

    public void printStructure() {
        tableIsNull();
        System.out.println(this.table.structure());
    }

    private void tableIsNull() {
        if(table == null) {
            try {
                throw new Exception("Table is null! Try loading the table again!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
