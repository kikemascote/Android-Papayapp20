package co.hipstercoding.dev.papayapp.utils;

import android.content.Context;

import co.hipstercoding.dev.papayapp.data.Section;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kenruizinoue on 9/12/17.
 */

public class SectionUtils {

    public static Section[] filterSectionById(int id, Context context) {
        Section[] sections = new DBUtils(context).getAllSectionsArray();

        List<Section> sectionList = new ArrayList<>();

        for (Section section : sections) {
            if (id == section.sectionId) {
                sectionList.add(section);
            }
        }

        sections = sectionList.toArray(new Section[0]);

        return sections;
    }
}
