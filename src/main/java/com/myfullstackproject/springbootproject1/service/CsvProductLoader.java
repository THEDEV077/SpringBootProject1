@Service
@Component
public class CsvProductLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    public CsvProductLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) return;

        try (InputStream is = getClass().getResourceAsStream("/Cleaned1.csv")) {
            // TODO: Lire CSV ligne par ligne
            // Parser ASIN, title, category, price, rating, reviewsCount, rank
            // Créer Product et saveAll()
        } catch (Exception e) {
            // log error
        }
    }
}