import com.dashamail.sdk.DashaMailApiException;
import com.dashamail.sdk.DashaMailClient;
import com.dashamail.sdk.DashaMailRateLimitException;
import com.dashamail.sdk.DashaMailResponse;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Run from the project root once the SDK is built, e.g.:
 *   mvn -q compile
 *   javac -cp target/classes -d /tmp/out examples/QuickStart.java
 *   java -cp target/classes:/tmp/out QuickStart
 */
public class QuickStart {

    public static void main(String[] args) {
        String apiKey = System.getenv().getOrDefault("DASHAMAIL_API_KEY", "YOUR_API_KEY");
        DashaMailClient dashamail = new DashaMailClient(apiKey);

        try {
            // Account balance.
            DashaMailResponse balance = dashamail.account().balance();
            System.out.println("Balance: " + balance.get("balance"));

            // Create a list and add a subscriber.
            DashaMailResponse created = dashamail.lists().create("Example list");
            Object listId = created.get("list_id");

            Map<String, Object> memberFields = new LinkedHashMap<>();
            memberFields.put("merge_1", "Иван");
            dashamail.lists().addMember(listId, "subscriber@example.com", memberFields);

            // Iterate subscribers, page by page.
            int start = 0;
            while (true) {
                Map<String, Object> page = new LinkedHashMap<>();
                page.put("start", start);
                page.put("limit", 100);
                DashaMailResponse members = dashamail.lists().members(listId, page);
                for (Object member : members) {
                    Map<?, ?> dict = (Map<?, ?>) member;
                    System.out.println(dict.get("email"));
                }
                start += members.getLimit() != null ? members.getLimit() : 0;
                if (!members.hasMore()) {
                    break;
                }
            }

            // Send a transactional email.
            Map<String, Object> sendParams = new LinkedHashMap<>();
            sendParams.put("subject", "Добро пожаловать");
            dashamail.transactional().send(
                "subscriber@example.com",
                "sender@yourdomain.com",
                "<p>Спасибо за подписку!</p>",
                sendParams
            );
        } catch (DashaMailRateLimitException e) {
            System.out.println("Rate limited, retry after " + e.getRetryAfter() + "s");
        } catch (DashaMailApiException e) {
            System.out.println("DashaMail API error " + e.getApiCode() + ": " + e.getMessage());
        }
    }
}
