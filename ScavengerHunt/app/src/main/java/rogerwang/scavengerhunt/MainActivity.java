package rogerwang.scavengerhunt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private EditText inputField;
    private Button addButton;
    private ListView itemList;

    private ArrayList<ScavengerItem> items;
    private MyArrayAdapter adapter;

    private int cameraInsertPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputField = (EditText) findViewById(R.id.inputText);
        addButton = (Button) findViewById(R.id.addButton);
        itemList = (ListView) findViewById(R.id.itemList);

        items = new ArrayList<ScavengerItem>();
        adapter = new MyArrayAdapter(this,items);

        itemList.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputField.getText().toString();
                inputField.setText("");
                items.add(new ScavengerItem(input));
                adapter.notifyDataSetChanged();
            }
        });

        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cameraInsertPos = position;
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(takePicture.resolveActivity(getBaseContext().getPackageManager()) != null)
                {
                    startActivityForResult(takePicture,REQUEST_PICTURE);
                }
            }
        });
    }

    //
    // ScavengerItem
    //
    // Container class for information about a scavenger hunt item.
    //
    private class ScavengerItem {
        private String name;
        private Bitmap icon;

        public ScavengerItem(String n){
            name = n;
            icon = null;
        }

        public ScavengerItem(String n, Bitmap b){
            name = n;
            icon = b;
        }

        public void setIcon(Bitmap b){
            icon = b;
        }

        public Bitmap getIcon(){
            return icon;
        }

        public String getName(){
            return name;
        }
    } // ScavengerItem

    //
    // MyArrayAdapter
    //
    // Custom adapter to populate listview rows with ScavengerItems.
    //
    private class MyArrayAdapter extends ArrayAdapter<ScavengerItem> {
        private Context context;
        private ArrayList<ScavengerItem> values;

        public MyArrayAdapter(Context c, ArrayList<ScavengerItem> v){
            super(c, R.layout.row, v);
            context = c;
            values = v;
        }

        public View getView(int pos, View convertView, ViewGroup parent){
            // System service that handles creating UI elements
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Create the specific row's view
            View row = inflater.inflate(R.layout.row, parent, false);

            // Initialize UI for row
            TextView name = (TextView) row.findViewById(R.id.nameText);
            ImageView pict = (ImageView) row.findViewById(R.id.pictView);

            // Assign UI data
            name.setText(values.get(pos).getName());

            if (values.get(pos).getIcon() != null){
                pict.setImageBitmap(values.get(pos).getIcon());
            }

            return row;
        }
    } // MyArrayAdapter

    static final int REQUEST_PICTURE = 1;

    // Return handler
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            items.get(cameraInsertPos).setIcon(imageBitmap);
            adapter.notifyDataSetChanged();
        }
    }
}
