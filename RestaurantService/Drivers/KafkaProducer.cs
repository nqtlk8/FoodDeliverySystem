using System.Text;
using Confluent.Kafka;

namespace RestaurantService.Drivers
{
    public class KafkaProducer
    {
        private readonly IConfiguration _configuration;
        private readonly ProducerConfig _config;

        public KafkaProducer(IConfiguration configuration)
        {
            _configuration = configuration;
            _config = new ProducerConfig
            {
                BootstrapServers = _configuration["Kafka:BootstrapServers"]
            };
        }

        public async Task PublishAsync(string topic, string key, string payload, string traceId, string userId)
        {
            using var producer = new ProducerBuilder<string, string>(_config).Build();

            var headers = new Headers
            {
                { "X-Trace-Id", Encoding.UTF8.GetBytes(traceId) },
                { "X-User-Id", Encoding.UTF8.GetBytes(userId) }
            };

            var message = new Message<string, string>
            {
                Key = key,
                Value = payload,
                Headers = headers
            };

            await producer.ProduceAsync(topic, message);
            Console.WriteLine($"[Kafka Producer] Đã phát sự kiện lên topic {topic} với TraceId: {traceId}");
        }
    }
}