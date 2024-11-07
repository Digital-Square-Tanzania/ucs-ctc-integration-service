package com.abt.actors;

import com.abt.domain.LtfClientRequest;
import com.abt.domain.Response;
import com.abt.util.OpenSrpService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.abt.util.OpenSrpService.sendDataToDestination;

public class SendLtfMissapProcessor {
    private final static Logger log =
            LoggerFactory.getLogger(SendLtfMissapProcessor.class);

    public Response sendLtfMissap(LtfClientRequest ltfClientRequest, String url
            , String username, String password) {
        String events;
        try {
            events = OpenSrpService.generateLtfEvent(ltfClientRequest, url,
                    username, password);
            assert events != null;
            JSONObject eventsObject = new JSONObject(events);
            return sendDataToDestination(events, url + "/opensrp/rest/event" +
                            "/add", username, password,
                    eventsObject.getJSONArray("clients").getJSONObject(1).getString("baseEntityId"),
                    eventsObject.getJSONArray("clients").getJSONObject(1).getJSONObject("identifiers").getString("opensrp_id"));
        } catch (Exception e) {
            log.error(e.getMessage());
            Response response = new Response();
            response.setDescription("Internal Error while processing the " +
                    "payload");
            return response;
        }
    }
}
