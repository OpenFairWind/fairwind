package it.uniparthenope.fairwind.sdk.captain.setup.ui;

import java.util.ArrayList;
import java.util.List;

import it.uniparthenope.fairwind.sdk.captain.setup.ui.Button;
import mjson.Json;

/**
 * Created by raffaelemontella on 14/09/2017.
 */

public class Ribbon extends ArrayList<Button> {

    public Ribbon() {

    }

    public Ribbon(Json json) {

        List<Json> list=json.asJsonList();
        for(Json item:list) {
            add(new Button(item));
        }
    }
}
