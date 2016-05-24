package hr.jbaketaricetfos.nickchat.Activitys.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import hr.jbaketaricetfos.nickchat.Activitys.Containers.MessageContainer;
import hr.jbaketaricetfos.nickchat.R;

/**
 * Created by Josip on 02.05.2016..
 */
public class MessageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MessageContainer> messageList;

    public MessageAdapter(Context context, ArrayList<MessageContainer> messageList) {
        super();
        this.messageList = messageList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            convertView = View.inflate(context, R.layout.message_custom_view, null);
        }

        MessageContainer current = messageList.get(position);

        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
        ImageView ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
        ivPicture.setImageDrawable(null);
        tvMessage.setText(null);

        tvUserName.setText(current.getName());
        if(current.getMessage() != null){
            tvMessage.setText(current.getMessage());
        }
        if(current.getPictureID() != null){
            ivPicture.setImageResource(current.getPictureID());
            ivPicture.setVisibility(View.VISIBLE);

        }

        return convertView;
    }
}
