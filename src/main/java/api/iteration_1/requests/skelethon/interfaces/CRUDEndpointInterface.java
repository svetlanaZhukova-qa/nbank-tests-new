package api.iteration_1.requests.skelethon.interfaces;

import api.iteration_1.models.BaseModel;

public interface CRUDEndpointInterface {
	Object post(BaseModel baseModel);
	Object get(long id);
	Object update(long id, BaseModel baseModel);
	Object delete(long id);
}
