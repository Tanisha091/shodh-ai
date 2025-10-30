FROM python:3.11-bookworm
RUN apt-get update && apt-get install -y --no-install-recommends openjdk-17-jdk-headless g++ && rm -rf /var/lib/apt/lists/*
WORKDIR /runner
COPY run_judge.py /runner/run_judge.py
RUN ln -sf /usr/bin/python3 /usr/bin/python
ENTRYPOINT ["python","/runner/run_judge.py"]