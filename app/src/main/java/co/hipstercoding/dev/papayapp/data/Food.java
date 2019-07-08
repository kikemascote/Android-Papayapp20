package co.hipstercoding.dev.papayapp.data;

import android.os.Parcel;
import android.os.Parcelable;


public class Food implements Parcelable {

    public final int foodId;
    public final String foodName;
    public final int foodUnit;
    public double foodQuantity;
    public final int foodCategory;
    public final String foodRegisteredTimestamp;
    public final String foodExpireDate;
    public int sectionId;

    public Food(int foodId, String foodName, int foodUnit, double foodQuantity, int foodCategory,
                String foodRegisteredTimestamp, String foodExpireDate, int sectionId) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodUnit = foodUnit;
        this.foodQuantity = foodQuantity;
        this.foodCategory = foodCategory;
        this.foodRegisteredTimestamp = foodRegisteredTimestamp;
        this.foodExpireDate = foodExpireDate;
        this.sectionId = sectionId;
    }


    private Food(Parcel in) {
        foodId = in.readInt();
        foodName = in.readString();
        foodUnit = in.readInt();
        foodQuantity = in.readDouble();
        foodCategory = in.readInt();
        foodRegisteredTimestamp = in.readString();
        foodExpireDate = in.readString();
        sectionId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(foodId);
        parcel.writeString(foodName);
        parcel.writeInt(foodUnit);
        parcel.writeDouble(foodQuantity);
        parcel.writeInt(foodCategory);
        parcel.writeString(foodRegisteredTimestamp);
        parcel.writeString(foodExpireDate);
        parcel.writeInt(sectionId);
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


}
