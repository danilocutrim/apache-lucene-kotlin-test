import prometheus_client
from prometheus_client import Enum
import time
import psutil

UPDATE_PERIOD = 1
SYSTEM_USAGE = prometheus_client.Gauge('system_usage',
                                       'Hold current system resource usage',
                                       ['resource_type','id'])
STATE = prometheus_client.Gauge('status',
                                               'Hold current system resource usage',
                                               ['resource_type','id'])
if __name__ == '__main__':
  prometheus_client.start_http_server(9999)
id = '1'
while True:
  STATE.labels('running', id).set(1.0)
  SYSTEM_USAGE.labels('cpu_percent', id).set(psutil.cpu_percent())
  SYSTEM_USAGE.labels('memory_available', id).set(psutil.virtual_memory().available)
  SYSTEM_USAGE.labels('memory_percent', id).set(psutil.virtual_memory().percent)
  SYSTEM_USAGE.labels('memory_used', id).set(psutil.virtual_memory().used)
  SYSTEM_USAGE.labels('memory_active', id).set(psutil.virtual_memory().active)
  SYSTEM_USAGE.labels('disk_usage_percent', id).set(psutil.disk_usage('/').percent)
  SYSTEM_USAGE.labels('bytes_sent', id).set(psutil.net_io_counters().bytes_sent)
  SYSTEM_USAGE.labels('bytes_recv', id).set(psutil.net_io_counters().bytes_recv)
  SYSTEM_USAGE.labels('started', id).set(psutil.boot_time())

  time.sleep(UPDATE_PERIOD)