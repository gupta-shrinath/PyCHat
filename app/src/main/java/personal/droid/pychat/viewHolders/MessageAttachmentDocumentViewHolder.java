package personal.droid.pychat.viewHolders;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import personal.droid.pychat.R;
import personal.droid.pychat.interfaces.OnMessageItemClick;
import personal.droid.pychat.models.Attachment;
import personal.droid.pychat.models.AttachmentTypes;
import personal.droid.pychat.models.Message;
import personal.droid.pychat.utils.FileUtils;
import personal.droid.pychat.utils.Helper;
import personal.droid.pychat.utils.MyFileProvider;

import java.io.File;

/**
 * Created by mayank on 11/5/17.
 */

public class MessageAttachmentDocumentViewHolder extends BaseMessageViewHolder {
    TextView fileExtention;
    TextView fileName;
    TextView fileSize;
    LinearLayout ll;
    CardView card_view;

    private Message message;
    private File file;

    public MessageAttachmentDocumentViewHolder(View itemView, OnMessageItemClick itemClickListener) {
        super(itemView, itemClickListener);
        fileExtention = itemView.findViewById(R.id.file_extention);
        fileName = itemView.findViewById(R.id.file_name);
        fileSize = itemView.findViewById(R.id.file_size);
        ll = itemView.findViewById(R.id.container);
        card_view = itemView.findViewById(R.id.card_view);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.CHAT_CAB)
                    downloadFile();
                onItemClick(true);
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClick(false);
                return true;
            }
        });
    }

    @Override
    public void setData(Message message, int position) {
        super.setData(message, position);
        card_view.setCardBackgroundColor(ContextCompat.getColor(context, message.isSelected() ? R.color.colorPrimary : R.color.colorBgLight));
        ll.setBackgroundColor(message.isSelected() ? ContextCompat.getColor(context, R.color.colorPrimary) : isMine() ? Color.WHITE : ContextCompat.getColor(context, R.color.colorBgLight));

        this.message = message;
        file = new File(Environment.getExternalStorageDirectory() + "/"
                +
                context.getString(R.string.app_name) + "/" + AttachmentTypes.getTypeName(message.getAttachmentType()) + (isMine() ? "/.sent/" : "")
                , message.getAttachment().getName());
        ll.setBackgroundColor(isMine() ? Color.WHITE : ContextCompat.getColor(context, R.color.colorBgLight));
        Attachment attachment = message.getAttachment();
        fileName.setText(attachment.getName());
        fileSize.setText(FileUtils.getReadableFileSize(attachment.getBytesCount()));
        fileExtention.setText(FileUtils.getExtension(attachment.getName()));
    }

    //@OnClick(R.id.download)
    public void downloadFile() {
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = MyFileProvider.getUriForFile(context,
                    context.getString(R.string.authority),
                    file);
            intent.setDataAndType(uri, Helper.getMimeType(context, uri)); //storage path is path of your vcf file and vFile is name of that file.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else if (!isMine())
            broadcastDownloadEvent();
        else
            Toast.makeText(context, "File unavailable", Toast.LENGTH_SHORT).show();
    }
}
