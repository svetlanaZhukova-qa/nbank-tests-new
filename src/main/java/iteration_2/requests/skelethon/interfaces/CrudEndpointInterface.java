package iteration_2.requests.skelethon.interfaces;

import iteration_2.models_body_JSON.BaseModel;

public interface CrudEndpointInterface {
	Object post(BaseModel baseModel);
	Object get(int id);
	Object update(long id, BaseModel baseModel);
	Object delete(long id);
}
