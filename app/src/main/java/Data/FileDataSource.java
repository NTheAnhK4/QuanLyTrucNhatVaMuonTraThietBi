package Data;

import android.content.Context;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileDataSource<T> implements DataSource<T> {
    private String fileName;
    private Context context;

    public FileDataSource(Context context, String fileName) {
        this.fileName = fileName;
        this.context = context;
    }

    @Override
    public void save(ArrayList<T> items) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(items);
            oos.close();
            fos.close();

        } catch (Exception e) {


        }

    }

    @Override
    public ArrayList<T> load() {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<T> listFromFile = (ArrayList<T>) ois.readObject();
            ois.close();
            fis.close();

            return listFromFile;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
