## install environment

$ `py -3.6 -m venv venv/`

$ `source venv/Scripts/activate`

$ `pip install -r api-requirements`

## start predict-api

$ `source venv/Scripts/activate` if (venv) not already activated

$ `uvicorn prediction-api:app --reload`
