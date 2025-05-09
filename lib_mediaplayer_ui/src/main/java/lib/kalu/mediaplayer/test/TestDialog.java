package lib.kalu.mediaplayer.test;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lib.kalu.mediaplayer.R;

public class TestDialog extends DialogFragment {

    public static String BUNDLE_DATA = "BUNDLE_DATA";
    public static String BUNDLE_TYPE = "BUNDLE_TYPE";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.module_mediaplayer_test_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {

            String string = getArguments().getString(BUNDLE_DATA, "{}");
            int type = getArguments().getInt(BUNDLE_TYPE, 1);

            JSONArray array = new JSONArray(string);
            int length = array.length();
            for (int i = 0; i < length; i++) {

                JSONObject object = array.getJSONObject(i);

                String curName;
                // 视频轨道
                if (type == 1) {
                    int bitrate = object.optInt("bitrate", 0);
                    int width = object.optInt("width", 0);
                    int height = object.optInt("height", 0);
                    String sampleMimeType = object.optString("sampleMimeType", "null");
                    int roleFlags = object.optInt("roleFlags", -1);
                    String language = object.optString("language", "null");
                    curName = "roleFlags = " + roleFlags + ", language = " + language + ", sampleMimeType = " + sampleMimeType + ", bitrate = " + bitrate + ", width = " + width + ", height = " + height;
                }
                // 音频轨道
                else if (type == 2) {
                    String sampleMimeType = object.optString("sampleMimeType", "null");
                    int roleFlags = object.optInt("roleFlags", -1);
                    String language = object.optString("language", "null");
                    curName = "roleFlags = " + roleFlags + ", language = " + language + ", sampleMimeType = " + sampleMimeType;
                }
                // 字幕轨道
                else if (type == 3) {
                    int roleFlags = object.optInt("roleFlags", -1);
                    String language = object.optString("language", "null");
                    curName = "roleFlags = " + roleFlags + ", language = " + language;
                } else {
                    continue;
                }

                RadioButton radioButton = new RadioButton(view.getContext());
                RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.MATCH_PARENT);
                radioButton.setLayoutParams(layoutParams);
                radioButton.setText(curName);
                radioButton.setTag(object);
                boolean isTrackSelected = object.optBoolean("isTrackSelected", false);
                boolean isTrackMixed = object.optBoolean("isTrackMixed", false);
                radioButton.setChecked(isTrackSelected);
                boolean isTrackSupported = object.optBoolean("isTrackSupported", false);
                radioButton.setEnabled(isTrackSupported);

                //
                RadioGroup radioGroup = view.findViewById(R.id.test_dialog_group);
                radioGroup.addView(radioButton);

                //
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Object tag = view.getTag();
                        JSONObject object = null;
                        try {
                            object = new JSONObject(tag.toString());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        // 视频轨道
                        if (type == 1) {
                            int groupIndex = object.optInt("groupIndex", -1);
                            int trackIndex = object.optInt("trackIndex", -1);
//                            int roleFlags = object.optInt("roleFlags", -1);
                            ((TestActivity) getActivity()).toggleTrack(groupIndex, trackIndex);
                        }
                        // 音频轨道
                        else if (type == 2) {
                            int roleFlags = object.optInt("roleFlags", -1);
                            ((TestActivity) getActivity()).toggleTrackRoleFlagAudio(roleFlags);
                        }
                        // 字幕轨道
                        else if (type == 3) {
//                            int roleFlags = object.optInt("roleFlags", -1);
//                            ((TestActivity) getActivity()).toggleTrackRoleFlagSubtitle(roleFlags);
                            int groupIndex = object.optInt("groupIndex", -1);
                            int trackIndex = object.optInt("trackIndex", -1);
//                            int roleFlags = object.optInt("roleFlags", -1);
                            ((TestActivity) getActivity()).toggleTrack(groupIndex, trackIndex);
                        }

                        //
                        dismiss();
                    }
                });
            }
        } catch (Exception e) {
        }
    }
}
