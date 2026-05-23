package iteration_2.requests.skelethon.interfaces;

import iteration_2.models_body_JSON.BaseModel;

public interface CrudEndpointInterface {
	Object post(BaseModel baseModel);
	Object getWithParams(int id);
	Object get();
	Object update(BaseModel baseModel);
	Object delete(long id);
}
