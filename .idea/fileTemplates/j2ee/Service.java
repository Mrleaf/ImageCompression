package ${PACKAGE_NAME};

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

#parse("File Header.java")
/**
 * Created by leaf on ${DATE}.
 */
public class ${NAME} extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
