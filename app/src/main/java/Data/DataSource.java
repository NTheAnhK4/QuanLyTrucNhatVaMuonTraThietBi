package Data;

import java.util.ArrayList;

public interface DataSource<T> {
    void save(ArrayList<T> items);
    ArrayList<T> load();
}
