package com.mercadolivre.product_api.infrastructure.initializer;

import com.mercadolivre.product_api.domain.model.Product;
import com.mercadolivre.product_api.domain.model.ProductImage;
import com.mercadolivre.product_api.domain.repository.ProductRepository;
import com.mercadolivre.product_api.infrastructure.repository.ProductImageRepositoryInMemory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ProductImageRepositoryInMemory productImageRepository;

    @Override
    public void run(String... args) {
        log.info("Inicializando dados de exemplo...");
        int productCount = 0;

        // ========== ELETRÔNICOS (30 produtos) ==========
        
        // Smartphones
        productCount += saveProduct("Smartphone Galaxy S23 Ultra", "Smartphone premium com tela AMOLED 6.8\", câmera 200MP e S Pen", "4999.99", 45, "eletronicos");
        productCount += saveProduct("iPhone 15 Pro Max", "iPhone com chip A17 Pro, câmera tripla 48MP e titânio", "7999.00", 30, "eletronicos");
        productCount += saveProduct("Xiaomi Redmi Note 13", "Smartphone intermediário com câmera 108MP e carregamento rápido", "1599.90", 80, "eletronicos");
        productCount += saveProduct("Motorola Edge 40", "Tela OLED 144Hz, processador Dimensity 8020 e 8GB RAM", "2199.00", 60, "eletronicos");
        productCount += saveProduct("Samsung Galaxy A54", "Smartphone com tela Super AMOLED 120Hz e bateria 5000mAh", "1899.00", 70, "eletronicos");
        
        // Notebooks e Computadores
        productCount += saveProduct("MacBook Pro M3", "Notebook Apple com chip M3, 16GB RAM e SSD 512GB", "12999.00", 20, "eletronicos");
        productCount += saveProduct("Dell XPS 15", "Notebook premium Intel i7, 32GB RAM, RTX 4060 e SSD 1TB", "9999.00", 25, "eletronicos");
        productCount += saveProduct("Lenovo IdeaPad Gaming 3", "Notebook gamer Ryzen 5, 16GB RAM, GTX 1650 e SSD 512GB", "3799.00", 40, "eletronicos");
        productCount += saveProduct("Acer Aspire 5", "Notebook i5, 8GB RAM, SSD 256GB ideal para trabalho", "2599.00", 55, "eletronicos");
        productCount += saveProduct("PC Gamer Completo", "i5-12400F, RTX 3060, 16GB RAM, SSD 480GB + HD 1TB", "4999.00", 15, "eletronicos");
        
        // Tablets
        productCount += saveProduct("iPad Air M2", "Tablet Apple 11\", chip M2, 128GB e Apple Pencil compatível", "5499.00", 35, "eletronicos");
        productCount += saveProduct("Samsung Galaxy Tab S9", "Tablet Android 11\", 120Hz, S Pen inclusa", "3299.00", 40, "eletronicos");
        
        // Áudio
        productCount += saveProduct("Fone Sony WH-1000XM5", "Headphone premium com cancelamento de ruído líder de mercado", "1799.00", 50, "eletronicos");
        productCount += saveProduct("AirPods Pro 2", "Fones Apple com cancelamento ativo de ruído e áudio espacial", "1999.00", 60, "eletronicos");
        productCount += saveProduct("JBL Flip 6", "Caixa de som portátil Bluetooth à prova d'água", "699.00", 100, "eletronicos");
        productCount += saveProduct("Soundbar Samsung HW-Q600C", "Soundbar 3.1.2 canais com Dolby Atmos", "1899.00", 30, "eletronicos");
        
        // TVs e Monitores
        productCount += saveProduct("Smart TV Samsung 55\" QLED", "TV 4K QLED com taxa de atualização 120Hz", "3299.00", 25, "eletronicos");
        productCount += saveProduct("Smart TV LG 65\" OLED", "TV OLED 4K com inteligência artificial e webOS", "6999.00", 15, "eletronicos");
        productCount += saveProduct("Monitor Gamer AOC 27\"", "Monitor 165Hz, 1ms, Full HD ideal para jogos", "899.00", 45, "eletronicos");
        productCount += saveProduct("Monitor Dell UltraSharp 32\"", "Monitor 4K IPS para profissionais criativos", "2799.00", 20, "eletronicos");
        
        // Periféricos
        productCount += saveProduct("Teclado Mecânico Keychron K2", "Teclado mecânico wireless com switches Gateron", "599.00", 80, "eletronicos");
        productCount += saveProduct("Mouse Logitech MX Master 3S", "Mouse ergonômico para produtividade", "549.00", 90, "eletronicos");
        productCount += saveProduct("Webcam Logitech C920", "Webcam Full HD 1080p com microfone estéreo", "399.00", 70, "eletronicos");
        
        // Smartwatches e Wearables
        productCount += saveProduct("Apple Watch Series 9", "Smartwatch Apple com GPS, monitoramento de saúde avançado", "3999.00", 40, "eletronicos");
        productCount += saveProduct("Samsung Galaxy Watch 6", "Smartwatch Android com monitoramento de sono e fitness", "1899.00", 50, "eletronicos");
        productCount += saveProduct("Xiaomi Mi Band 8", "Pulseira inteligente com monitoramento de atividades", "249.00", 150, "eletronicos");
        
        // Câmeras
        productCount += saveProduct("GoPro Hero 12 Black", "Câmera de ação 5.3K com estabilização HyperSmooth", "2799.00", 30, "eletronicos");
        productCount += saveProduct("DJI Mini 3 Pro", "Drone compacto com câmera 4K e 34min de voo", "4299.00", 20, "eletronicos");
        
        // Acessórios
        productCount += saveProduct("Carregador Anker 65W", "Carregador rápido USB-C com GaN technology", "199.00", 120, "eletronicos");
        productCount += saveProduct("SSD Externo Samsung T7", "SSD portátil 1TB com velocidade de até 1050MB/s", "599.00", 60, "eletronicos");

        // ========== MODA (25 produtos) ==========
        
        // Camisetas e Tops
        productCount += saveProduct("Camiseta Básica Premium", "Camiseta 100% algodão egípcio, várias cores", "79.90", 300, "moda");
        productCount += saveProduct("Camiseta Oversized", "Camiseta estilo oversized, modelagem moderna", "89.90", 200, "moda");
        productCount += saveProduct("Regata Fitness Dry Fit", "Regata respirável para treinos intensos", "59.90", 250, "moda");
        productCount += saveProduct("Camisa Social Slim", "Camisa social slim fit em algodão premium", "149.00", 100, "moda");
        productCount += saveProduct("Polo Ralph Lauren", "Camisa polo clássica com logo bordado", "349.00", 80, "moda");
        
        // Calças e Shorts
        productCount += saveProduct("Calça Jeans Skinny", "Jeans escuro com elastano, modelagem skinny", "199.00", 150, "moda");
        productCount += saveProduct("Calça Cargo", "Calça cargo com bolsos laterais, estilo urbano", "179.00", 120, "moda");
        productCount += saveProduct("Bermuda Moletom", "Bermuda confortável de moletom premium", "129.00", 180, "moda");
        productCount += saveProduct("Shorts Jeans", "Shorts jeans destroyed, estilo despojado", "99.00", 200, "moda");
        
        // Calçados
        productCount += saveProduct("Tênis Nike Air Max", "Tênis casual com tecnologia Air Max", "699.00", 60, "moda");
        productCount += saveProduct("Tênis Adidas Ultraboost", "Tênis de corrida com tecnologia Boost", "799.00", 50, "moda");
        productCount += saveProduct("Tênis Vans Old Skool", "Tênis clássico de skate, icônico", "399.00", 90, "moda");
        productCount += saveProduct("Bota Coturno", "Bota coturno em couro legítimo", "449.00", 40, "moda");
        productCount += saveProduct("Chinelo Havaianas Top", "Chinelo de borracha confortável", "39.90", 500, "moda");
        
        // Acessórios
        productCount += saveProduct("Boné New Era", "Boné aba reta bordado", "149.00", 150, "moda");
        productCount += saveProduct("Óculos de Sol Ray-Ban", "Óculos Wayfarer clássico com proteção UV", "599.00", 70, "moda");
        productCount += saveProduct("Relógio Casio G-Shock", "Relógio digital resistente a impactos", "899.00", 45, "moda");
        productCount += saveProduct("Mochila Herschel", "Mochila urbana com compartimento para notebook", "499.00", 80, "moda");
        productCount += saveProduct("Carteira de Couro", "Carteira masculina em couro legítimo", "129.00", 120, "moda");
        productCount += saveProduct("Cinto de Couro", "Cinto reversível preto/marrom", "99.00", 150, "moda");
        
        // Roupas Femininas
        productCount += saveProduct("Vestido Longo Floral", "Vestido longo estampado para verão", "229.00", 60, "moda");
        productCount += saveProduct("Blazer Feminino", "Blazer alfaiataria para look executivo", "349.00", 50, "moda");
        productCount += saveProduct("Legging Fitness", "Legging de alta compressão para treinos", "149.00", 200, "moda");
        productCount += saveProduct("Top Cropped", "Top cropped canelado em diversas cores", "59.90", 250, "moda");
        productCount += saveProduct("Saia Midi Plissada", "Saia plissada elegante", "179.00", 80, "moda");

        // ========== CASA E DECORAÇÃO (20 produtos) ==========
        
        // Móveis Sala
        productCount += saveProduct("Sofá Retrátil 3 Lugares", "Sofá retrátil e reclinável em tecido suede", "2499.00", 15, "casa-decoracao");
        productCount += saveProduct("Sofá de Canto", "Sofá de canto 5 lugares com chaise", "3299.00", 10, "casa-decoracao");
        productCount += saveProduct("Rack para TV 55\"", "Rack moderno com portas e prateleiras", "599.00", 30, "casa-decoracao");
        productCount += saveProduct("Estante Industrial", "Estante de madeira e metal estilo industrial", "799.00", 25, "casa-decoracao");
        productCount += saveProduct("Mesa de Centro", "Mesa de centro em vidro temperado", "499.00", 40, "casa-decoracao");
        
        // Móveis Quarto
        productCount += saveProduct("Cama Box Casal", "Cama box casal com colchão molas ensacadas", "1999.00", 20, "casa-decoracao");
        productCount += saveProduct("Guarda-Roupa 6 Portas", "Guarda-roupa espaçoso com espelho", "1599.00", 15, "casa-decoracao");
        productCount += saveProduct("Criado-Mudo", "Criado-mudo com 2 gavetas", "299.00", 50, "casa-decoracao");
        productCount += saveProduct("Colchão Ortobom King", "Colchão ortopédico de molas ensacadas", "2799.00", 12, "casa-decoracao");
        
        // Móveis Cozinha/Jantar
        productCount += saveProduct("Mesa de Jantar 6 Lugares", "Mesa de jantar em madeira maciça com cadeiras", "1899.00", 18, "casa-decoracao");
        productCount += saveProduct("Buffet Cozinha", "Buffet com portas e gavetas para cozinha", "899.00", 25, "casa-decoracao");
        productCount += saveProduct("Jogo de Cadeiras", "4 cadeiras estofadas para sala de jantar", "699.00", 30, "casa-decoracao");
        
        // Decoração
        productCount += saveProduct("Tapete Sala 2x3m", "Tapete decorativo antialérgico", "399.00", 40, "casa-decoracao");
        productCount += saveProduct("Luminária de Piso", "Luminária moderna com dimmer", "449.00", 35, "casa-decoracao");
        productCount += saveProduct("Espelho Decorativo", "Espelho redondo com moldura dourada", "299.00", 45, "casa-decoracao");
        productCount += saveProduct("Kit Quadros Decorativos", "3 quadros abstratos para sala", "249.00", 60, "casa-decoracao");
        productCount += saveProduct("Cortina Blackout", "Cortina blackout 3 metros", "199.00", 70, "casa-decoracao");
        productCount += saveProduct("Jogo de Cama Casal", "Jogo de cama 300 fios 100% algodão", "349.00", 80, "casa-decoracao");
        productCount += saveProduct("Kit Almofadas Decorativas", "4 almofadas decorativas variadas", "159.00", 90, "casa-decoracao");
        productCount += saveProduct("Vaso Decorativo Grande", "Vaso de cerâmica para plantas", "179.00", 50, "casa-decoracao");

        // ========== LIVROS (15 produtos) ==========
        
        // Ficção
        productCount += saveProduct("O Senhor dos Anéis - Box", "Trilogia completa de J.R.R. Tolkien", "149.90", 50, "livros");
        productCount += saveProduct("Harry Potter - Coleção", "Coleção completa 7 livros", "249.00", 40, "livros");
        productCount += saveProduct("1984 - George Orwell", "Clássico distópico sobre totalitarismo", "49.90", 100, "livros");
        productCount += saveProduct("O Hobbit", "A jornada de Bilbo Bolseiro", "39.90", 80, "livros");
        
        // Desenvolvimento Pessoal
        productCount += saveProduct("Hábitos Atômicos", "Como criar bons hábitos e se livrar dos maus", "54.90", 120, "livros");
        productCount += saveProduct("Mindset", "A nova psicologia do sucesso", "47.90", 90, "livros");
        productCount += saveProduct("O Poder do Hábito", "Por que fazemos o que fazemos", "49.90", 85, "livros");
        productCount += saveProduct("Pai Rico Pai Pobre", "Educação financeira pessoal", "44.90", 150, "livros");
        
        // Técnicos
        productCount += saveProduct("Clean Code", "Código limpo: Habilidades práticas do Agile Software", "89.90", 60, "livros");
        productCount += saveProduct("Design Patterns", "Padrões de projeto: soluções reutilizáveis", "99.90", 45, "livros");
        productCount += saveProduct("Algoritmos - Cormen", "Introdução aos algoritmos - 3ª edição", "249.00", 30, "livros");
        
        // Romance
        productCount += saveProduct("A Culpa é das Estrelas", "Romance jovem adulto emocionante", "39.90", 70, "livros");
        productCount += saveProduct("Orgulho e Preconceito", "Clássico de Jane Austen", "34.90", 65, "livros");
        productCount += saveProduct("Como Eu Era Antes de Você", "Romance best-seller", "42.90", 75, "livros");
        productCount += saveProduct("O Morro dos Ventos Uivantes", "Clássico gótico inglês", "37.90", 55, "livros");

        log.info("Dados de exemplo inicializados com sucesso! {} produtos criados.", productCount);
    }
    
    private int saveProduct(String name, String description, String price, Integer quantity, String category) {
        Product product = createProduct(name, description, new BigDecimal(price), quantity, category);
        productRepository.save(product);
        return 1;
    }

    private Product createProduct(String name, String description, BigDecimal price, Integer quantity, String category) {
        return Product.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .description(description)
                .price(price)
                .quantity(quantity)
                .category(category)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private void createImage(String productId, String url, String altText, Boolean isPrimary, Integer displayOrder) {
        ProductImage image = ProductImage.builder()
                .id(UUID.randomUUID().toString())
                .productId(productId)
                .url(url)
                .altText(altText)
                .isPrimary(isPrimary)
                .displayOrder(displayOrder)
                .build();
        
        productImageRepository.findAll().add(image);
    }

}
