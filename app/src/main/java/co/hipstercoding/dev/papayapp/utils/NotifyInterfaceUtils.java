package co.hipstercoding.dev.papayapp.utils;

import co.hipstercoding.dev.papayapp.data.Food;

/**
 * Created by kenruizinoue on 9/12/17.
 */

public interface NotifyInterfaceUtils {

    void onAddFood();
    void onAddSection();
    void updateUi();
    void deleteFood(Food deletedFood);

}
