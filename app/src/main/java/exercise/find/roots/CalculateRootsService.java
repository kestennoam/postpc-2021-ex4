package exercise.find.roots;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CalculateRootsService extends IntentService {

    public static final String ACTION_SUCCESS_ROOTS = "found_roots";
    public static final String ACTION_FAILURE_ROOTS = "not_found_roots";

    public CalculateRootsService() {
        super("CalculateRootsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        long timeStartMs = System.currentTimeMillis();
        long numberToCalculateRootsFor = intent.getLongExtra("number_for_service", 0);
        if (numberToCalculateRootsFor <= 0) {
            Log.e("CalculateRootsService", "can't calculate roots for non-positive input" + numberToCalculateRootsFor);
            return;
        }

        // check if 2
        if (numberToCalculateRootsFor == 2){
            sendSuccessPrimeBroadcast(System.currentTimeMillis() - timeStartMs, numberToCalculateRootsFor);
        }

        // check if even
        else if (numberToCalculateRootsFor % 2 == 0) {
            System.out.println("even");
            sendSuccessBroadcast(System.currentTimeMillis() - timeStartMs, 2, numberToCalculateRootsFor / 2, numberToCalculateRootsFor);
            return;
        }

        // calculating roots (enough until square without even numbers)
        for (long i = 3; i <= Math.sqrt(numberToCalculateRootsFor); i += 2) {
            // check timeout
            long elapsedTime = System.currentTimeMillis() - timeStartMs;
            if (elapsedTime >= 20000) {
                System.out.println("failure");
                sendFailureBroadcast(elapsedTime, numberToCalculateRootsFor);
                return;
            }
            // check if dividing number
            if (numberToCalculateRootsFor % i == 0) {
                System.out.println("has roots");
                sendSuccessBroadcast(System.currentTimeMillis() - timeStartMs, i, numberToCalculateRootsFor / i, numberToCalculateRootsFor);
                return;

            }
        }

        System.out.println("prime");
        sendSuccessPrimeBroadcast(System.currentTimeMillis() - timeStartMs, numberToCalculateRootsFor);

    }

    private void sendSuccessPrimeBroadcast(long elapsedTime, long originalNumber) {
        Intent broadcastIntent = new Intent(CalculateRootsService.ACTION_SUCCESS_ROOTS);
        broadcastIntent.putExtra("original_number", originalNumber);
        broadcastIntent.putExtra("is_prime", true);
        broadcastIntent.putExtra("elapsed_time", elapsedTime);
        sendBroadcast(broadcastIntent);
    }

    private void sendSuccessBroadcast(long elapsedTime, long root1, long root2, long originalNumber) {
        Intent broadcastIntent = new Intent(CalculateRootsService.ACTION_SUCCESS_ROOTS);
        broadcastIntent.putExtra("original_number", originalNumber);
        broadcastIntent.putExtra("root1", root1);
        broadcastIntent.putExtra("root2", root2);
        broadcastIntent.putExtra("is_prime", false);
        broadcastIntent.putExtra("elapsed_time", elapsedTime);

        sendBroadcast(broadcastIntent);
    }

    private void sendFailureBroadcast(long elapsedTime, long originalNumber) {
        Intent broadcastIntent = new Intent(CalculateRootsService.ACTION_FAILURE_ROOTS);
        broadcastIntent.putExtra("original_number", originalNumber);
        broadcastIntent.putExtra("elapsed_time", elapsedTime);

        sendBroadcast(broadcastIntent);

    }

    /*
    TODO:
     calculate the roots.
     check the time (using `System.currentTimeMillis()`) and stop calculations if can't find an answer after 20 seconds
     upon success (found a root, or found that the input number is prime):
      send broadcast with action "found_roots" and with extras:
       - "original_number"(long)
       - "root1"(long)
       - "root2"(long)
     upon failure (giving up after 20 seconds without an answer):
      send broadcast with action "stopped_calculations" and with extras:
       - "original_number"(long)
       - "time_until_give_up_seconds"(long) the time we tried calculating

      examples:
       for input "33", roots are (3, 11)
       for input "30", roots can be (3, 10) or (2, 15) or other options
       for input "17", roots are (17, 1)
       for input "829851628752296034247307144300617649465159", after 20 seconds give up

     */

}