package Data;

import java.util.ArrayList;
import java.util.UUID;

public class Repository<T extends Identifiable> {
    private ArrayList<T> items = new ArrayList<>();
    private DataSource<T> dataSource;

    public Repository(DataSource<T> dataSource){
        this.dataSource = dataSource;
        items = dataSource.load();
    }
    public void add(T item){

        item.setId(UUID.randomUUID().toString());
        items.add(item);
        dataSource.save(items);

    }
    public void remove(T item){
        items.removeIf(i -> i.getId().equals(item.getId()));
        dataSource.save(items);
    }
    public void update(T item){
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(item.getId())) {
                items.set(i, item);
                dataSource.save(items);
                return;
            }
        }
    }
    public T getItemByID(int id){
        if(id < 0 || id > items.size()) return null;
        return items.get(id);
    }
    public ArrayList<T> getAll() {
        return items;
    }
}
