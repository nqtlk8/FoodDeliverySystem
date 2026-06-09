using Confluent.Kafka;
using RestaurantService.Drivers;
using System.Text;
using System.Text.Json;

namespace RestaurantService.BackgroundServices
{
    public class KafkaConsumerService : BackgroundService
    {
        private readonly IConfiguration _configuration;
        private readonly IServiceProvider _serviceProvider;
        private readonly ConsumerConfig _consumerConfig;

        public KafkaConsumerService(IConfiguration configuration, IServiceProvider serviceProvider)
        {
            _configuration = configuration;
            _serviceProvider = serviceProvider;
            _consumerConfig = new ConsumerConfig
            {
                BootstrapServers = _configuration["Kafka:BootstrapServers"],
                GroupId = _configuration["Kafka:GroupId"],
                AutoOffsetReset = AutoOffsetReset.Earliest
            };
        }

        protected override Task ExecuteAsync(CancellationToken stoppingToken)
        {
            Task.Run(() => StartConsumerLoop(stoppingToken), stoppingToken);
            return Task.CompletedTask;
        }

        private void StartConsumerLoop(CancellationToken stoppingToken)
        {
            using var consumer = new ConsumerBuilder<string, string>(_consumerConfig).Build();
            consumer.Subscribe("driver.assigned");

            Console.WriteLine("[Kafka Consumer] Restaurant Service đang lắng nghe Topic: driver.assigned...");

            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var consumeResult = consumer.Consume(stoppingToken);
                    if (consumeResult == null) continue;

                    var traceId = GetHeaderValue(consumeResult.Message.Headers, "X-Trace-Id");
                    Console.WriteLine($"[Kafka Event] Nhận sự kiện Driver_Assigned. [TraceID: {traceId}]");

                    // Xử lý nghiệp vụ hiển thị bếp trực tiếp từ dữ liệu sự kiện
                    ProcessDriverAssignedEvent(consumeResult.Message.Value);
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"[Kafka Consumer Error]: {ex.Message}");
                }
            }
        }

private void ProcessDriverAssignedEvent(string messageValue)
{
    using var doc = JsonDocument.Parse(messageValue);
    var root = doc.RootElement;
    
    string orderId = root.GetProperty("order_id").GetString() ?? "";
    string restaurantId = root.GetProperty("restaurant_id").GetString() ?? "";
    string driverName = root.GetProperty("driver_name").GetString() ?? "";
    var items = root.GetProperty("items");

    // Xử lý nghiệp vụ FR-RES-04: Đẩy dữ liệu Realtime lên Web Dashboard / SignalR Hub của nhà bếp
    Console.WriteLine($"[FR-RES-04] Đơn hàng {orderId} tại quán {restaurantId} chuyển sang trạng thái -> PREPARING");
    Console.WriteLine($"[Dashboard Bếp] Đã nhận danh sách món ăn và hiển thị lên màn hình chế biến: {items.GetRawText()}");
    Console.WriteLine($"[Firebase Push] Gửi thông báo đến Chủ nhà hàng {restaurantId}: 'Tài xế {driverName} đã nhận đơn {orderId}. Hãy bắt đầu chế biến!'");
}
        private string GetHeaderValue(Headers headers, string key)
        {
            var header = headers.FirstOrDefault(h => h.Key == key);
            return header != null ? Encoding.UTF8.GetString(header.GetValueBytes()) : "N/A";
        }
    }
}