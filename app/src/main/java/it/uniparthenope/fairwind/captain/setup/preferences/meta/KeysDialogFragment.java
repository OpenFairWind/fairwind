package it.uniparthenope.fairwind.captain.setup.preferences.meta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.Key;

/**
 * Created by raffaelemontella on 09/02/2017.
 */

public class KeysDialogFragment extends DialogFragment  {
    public static final String LOG_TAG="...ADDNEW...FRAGMENT";

    private MetaPreferencesDialog metaPreferencesDialog;


    private TextView tvPath;


    public KeysDialogFragment() {

    }

    public void setMetaPreferencesDialog(MetaPreferencesDialog metaPreferencesDialog) {
        this.metaPreferencesDialog = metaPreferencesDialog;
    }

    private KeysContent keysContent;
    private LeafRecyclerViewAdapter leafRecyclerViewAdapter;
    private ChildrenRecyclerViewAdapter childrenRecyclerViewAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        keysContent=new KeysContent(getResources());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View keysView = inflater.inflate(R.layout.dialog_keys, null);

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        keysView.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
        keysView.setMinimumHeight((int)(displayRectangle.height() * 0.9f));

        final RecyclerView childrenRecyclerView=(RecyclerView)keysView.findViewById(R.id.list_children);
        final RecyclerView leafRecyclerView=(RecyclerView)keysView.findViewById(R.id.list_leaf);

        final Button buttonUp=(Button)keysView.findViewById(R.id.button_up);
        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (keysContent.getPath().isEmpty()==false) {
                    keysContent.getPath().pop();
                    updatePath();
                }
            }
        });

        tvPath=(TextView)keysView.findViewById(R.id.item_path);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Keys with metadata");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setView(keysView);

        tvPath.setText(keysContent.getPathAsString());
        childrenRecyclerViewAdapter = new ChildrenRecyclerViewAdapter(this, keysContent);
        leafRecyclerViewAdapter = new LeafRecyclerViewAdapter(this, keysContent);


        childrenRecyclerView.setLayoutManager(new LinearLayoutManager(builder.getContext()));
        childrenRecyclerView.setAdapter(childrenRecyclerViewAdapter);

        leafRecyclerView.setLayoutManager(new LinearLayoutManager(builder.getContext()));
        leafRecyclerView.setAdapter(leafRecyclerViewAdapter);

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }});

        // Create the AlertDialog object and return it
        return builder.create();

    }

    public void onLeafSelection(Key item) {
        metaPreferencesDialog.fetchKey(item);
        dismiss();
    }


    public void onChildrenSelection(Key item) {
        String[] parts=item.getPath().split("/");
        String last=parts[parts.length-1];
        keysContent.getPath().push(last);
        updatePath();
    }

    private void updatePath() {
        tvPath.setText(keysContent.getPathAsString().replace("//","/"));

        childrenRecyclerViewAdapter.setContent(keysContent);
        leafRecyclerViewAdapter.setContent(keysContent);

        childrenRecyclerViewAdapter.notifyDataSetChanged();
        leafRecyclerViewAdapter.notifyDataSetChanged();
    }
}
