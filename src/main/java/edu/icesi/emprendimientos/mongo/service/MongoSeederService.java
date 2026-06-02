package edu.icesi.emprendimientos.mongo.service;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.mongo.document.CalificacionDocument;
import edu.icesi.emprendimientos.mongo.document.EmprendimientoDocument;
import edu.icesi.emprendimientos.mongo.document.UsuarioSyncDocument;
import edu.icesi.emprendimientos.mongo.repository.*;
import edu.icesi.emprendimientos.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class MongoSeederService {

    private static final Logger log = LoggerFactory.getLogger(MongoSeederService.class);

    private final UsuarioRepository             usuarioRepository;
    private final MongoSyncService              syncService;
    private final EmprendimientoMongoRepository empMongoRepo;
    private final UsuarioSyncMongoRepository    usuarioSyncRepo;
    private final CalificacionMongoRepository   calificacionRepo;

    public MongoSeederService(UsuarioRepository usuarioRepository,
                              MongoSyncService syncService,
                              EmprendimientoMongoRepository empMongoRepo,
                              UsuarioSyncMongoRepository usuarioSyncRepo,
                              CalificacionMongoRepository calificacionRepo) {
        this.usuarioRepository = usuarioRepository;
        this.syncService       = syncService;
        this.empMongoRepo      = empMongoRepo;
        this.usuarioSyncRepo   = usuarioSyncRepo;
        this.calificacionRepo  = calificacionRepo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedMongoFromPostgres() {
        try {
            seedUsuarios();
            seedEmprendimientos();
            seedCalificaciones();
        } catch (Exception e) {
            log.warn("MongoDB seeding skipped or partial: {}", e.getMessage());
        }
    }

    /** Llamar desde controller para forzar re-siembra completa */
    public void forceReseed() {
        log.info("=== FORCE RESEED: limpiando MongoDB y resembrando... ===");
        calificacionRepo.deleteAll();
        empMongoRepo.deleteAll();
        usuarioSyncRepo.deleteAll();
        seedUsuarios();
        seedEmprendimientos();
        seedCalificaciones();
        log.info("=== FORCE RESEED completado ===");
    }

    // ── 1. Usuarios desde PostgreSQL (Supabase) ───────────────────
    private void seedUsuarios() {
        if (usuarioSyncRepo.count() > 0) { log.info("MongoDB Usuarios: ya tiene datos, skip."); return; }
        List<Usuario> usuarios = usuarioRepository.findAll();
        log.info("Sincronizando {} usuarios a MongoDB...", usuarios.size());
        usuarios.forEach(u -> { try { syncService.sincronizarUsuario(u); } catch (Exception e) { log.warn("Skip {}: {}", u.getIdUsuario(), e.getMessage()); } });
        log.info("Usuarios sincronizados a MongoDB.");
    }

    // ── 2. Emprendimientos con productos embebidos ────────────────
    private void seedEmprendimientos() {
        if (empMongoRepo.count() > 0) { log.info("MongoDB Emprendimientos: ya tiene datos, skip."); return; }
        log.info("Sembrando emprendimientos en MongoDB...");
        empMongoRepo.saveAll(buildEmprendimientos());
        log.info("Emprendimientos sembrados en MongoDB.");
    }

    // ── 3. Calificaciones ─────────────────────────────────────────
    private void seedCalificaciones() {
        if (calificacionRepo.count() > 0) { log.info("MongoDB Calificaciones: ya tiene datos, skip."); return; }
        List<EmprendimientoDocument> emps = empMongoRepo.findAll();
        if (emps.isEmpty()) return;
        Map<Integer, String> empIdMap = new HashMap<>();
        emps.forEach(e -> empIdMap.put(e.getIdEmprendimientoSql(), e.getId()));
        List<CalificacionDocument> cals = new ArrayList<>();
        addCal(cals, 1, 1, empIdMap, 2,  5, "Excelente servicio! El mouse llego perfecto.",        4, 19);
        addCal(cals, 2, 3, empIdMap, 3,  5, "El cappuccino esta increible!",                         4, 14);
        addCal(cals, 3, 3, empIdMap, 8,  5, "Siempre fresco y delicioso.",                           6, 17);
        addCal(cals, 4, 3, empIdMap, 11, 3, "Bueno pero la entrega se demoro.",                      7, 21);
        addCal(cals, 5, 4, empIdMap, 6,  4, "Los stickers quedaron hermosos.",                       8, 28);
        addCal(cals, 6, 5, empIdMap, 10, 4, "Muy rico el bowl proteico, porcion generosa.",          4,  7);
        calificacionRepo.saveAll(cals);
        log.info("Calificaciones sembradas en MongoDB.");
        for (int idEmp : new int[]{1, 3, 4, 5}) {
            try { syncService.actualizarMetricasCalificacion(idEmp, null); } catch (Exception ex) { }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────

    private void addCal(List<CalificacionDocument> list, int idCal, int idEmpSql,
                        Map<Integer, String> empIdMap, int idPedido, int puntuacion,
                        String comentario, int idUsuarioSql, int diasAtras) {
        // Get real user name from MongoDB
        String nombreUsuario = usuarioSyncRepo.findByIdUsuarioSql(idUsuarioSql)
            .map(UsuarioSyncDocument::getNombreCompleto).orElse("Usuario " + idUsuarioSql);
        CalificacionDocument cal = new CalificacionDocument();
        cal.setIdCalificacionSql(idCal);
        cal.setIdEmprendimientoSql(idEmpSql);
        cal.setEmprendimientoId(empIdMap.getOrDefault(idEmpSql, null));
        cal.setIdPedidoSql(idPedido);
        cal.setUsuario(new CalificacionDocument.UsuarioEmbed(idUsuarioSql, nombreUsuario));
        cal.setPuntuacion(puntuacion);
        cal.setComentario(comentario);
        Calendar c = Calendar.getInstance(); c.add(Calendar.DAY_OF_YEAR, -diasAtras);
        cal.setFecha(c.getTime());
        cal.setCreatedAt(new Date());
        list.add(cal);
    }

    private List<EmprendimientoDocument> buildEmprendimientos() {
        Date now = new Date();
        List<EmprendimientoDocument> list = new ArrayList<>();

        // TechStore — Tecnologia (usuario 1: Ximena)
        EmprendimientoDocument ts = emp(1,"TechStore","Accesorios y gadgets tecnologicos de calidad.",
            "Tecnologia",1,1,"https://images.unsplash.com/photo-1518770660439-4636190af475?w=300&h=300&fit=crop",true,now);
        ts.setProductos(List.of(
            prod(1,"iPhone 13 Reacondicionado","iPhone 13 en perfecto estado, bateria al 92%.",3500000,3,"ACTIVO",
                "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400&h=300&fit=crop"),
            prod(2,"AirPods Pro 2da Gen","Audifonos Apple con cancelacion de ruido.",980000,6,"ACTIVO",
                "https://images.unsplash.com/photo-1603351154351-5e2d0600bb77?w=400&h=300&fit=crop"),
            prod(3,"Cargador USB-C 65W","Cargador rapido GaN compatible con laptops.",48000,20,"ACTIVO",
                "https://images.unsplash.com/photo-1601524909162-ae8725290836?w=400&h=300&fit=crop"),
            prod(4,"Mouse Logitech MX Master 3","Mouse ergonomico de alta precision.",180000,8,"ACTIVO",
                "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400&h=300&fit=crop"),
            prod(5,"Hub USB-C 7 en 1","Hub multipuerto con HDMI 4K.",85000,12,"ACTIVO",
                "https://images.unsplash.com/photo-1625842268584-8f3296236761?w=400&h=300&fit=crop")
        ));
        list.add(ts);

        // IcesiWear — Moda (usuario 2: Carlos)
        EmprendimientoDocument iw = emp(2,"IcesiWear","Ropa urbana y accesorios de moda universitaria.",
            "Moda",2,2,"https://images.unsplash.com/photo-1445205170230-053b83016050?w=300&h=300&fit=crop",true,now);
        iw.setProductos(List.of(
            prod(7,"Camiseta Oversize Bordada","Camiseta algodon con bordado exclusivo.",55000,28,"ACTIVO",
                "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400&h=300&fit=crop"),
            prod(8,"Tote Bag Canvas Premium","Bolsa lona reforzada con estampado.",38000,15,"ACTIVO",
                "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=400&h=300&fit=crop"),
            prod(9,"Hoodie Universitario","Sudadera premium con capucha y logo bordado.",95000,10,"ACTIVO",
                "https://images.unsplash.com/photo-1620799140188-3b2a02fd9a77?w=400&h=300&fit=crop"),
            prod(10,"Gorra Snapback Icesi","Gorra snapback con parche bordado.",42000,20,"ACTIVO",
                "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=400&h=300&fit=crop")
        ));
        list.add(iw);

        // CafeIcesi — Bebidas (usuario 3: Ana)
        EmprendimientoDocument ci = emp(3,"CafeIcesi","Cafe de especialidad preparado por estudiantes.",
            "Bebidas",4,3,"https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=300&h=300&fit=crop",true,now);
        ci.setProductos(List.of(
            prod(12,"Cappuccino Especial","Doble espresso con leche de avena y canela.",9500,50,"ACTIVO",
                "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=400&h=300&fit=crop"),
            prod(13,"Latte de Caramelo","Espresso con sirope artesanal de caramelo.",10500,40,"ACTIVO",
                "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=300&fit=crop"),
            prod(14,"Cold Brew 500ml","Cafe de filtracion en frio por 18 horas.",12000,25,"ACTIVO",
                "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400&h=300&fit=crop"),
            prod(15,"Pack Brownies x6","Brownies artesanales de chocolate belga.",28000,18,"ACTIVO",
                "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=400&h=300&fit=crop")
        ));
        list.add(ci);

        // ArteU — Arte (usuario 3: Ana)
        EmprendimientoDocument au = emp(4,"ArteU","Ilustraciones y productos artisticos con identidad universitaria.",
            "Arte",5,3,"https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=300&h=300&fit=crop",false,now);
        au.setProductos(List.of(
            prod(17,"Sticker Pack x10","Set de 10 stickers con ilustraciones de Icesi.",12000,60,"ACTIVO",
                "https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=400&h=300&fit=crop"),
            prod(18,"Ilustracion Personalizada","Retrato digital en estilo caricatura.",45000,10,"ACTIVO",
                "https://images.unsplash.com/photo-1605721911519-3dfeb3be25e7?w=400&h=300&fit=crop"),
            prod(19,"Poster Arte A3","Poster impreso en papel satinado 200g.",22000,25,"ACTIVO",
                "https://images.unsplash.com/photo-1547036967-23d11aacaee0?w=400&h=300&fit=crop")
        ));
        list.add(au);

        // FoodLab — Comida (usuario 5: Sofia)
        EmprendimientoDocument fl = emp(5,"FoodLab Icesi","Comida saludable preparada por estudiantes de Gastronomia.",
            "Comida",3,5,"https://images.unsplash.com/photo-1547592180-85f173990554?w=300&h=300&fit=crop",true,now);
        fl.setProductos(List.of(
            prod(21,"Bowl Proteico Pollo","Bowl de quinoa, pollo al limon y aguacate.",18000,20,"ACTIVO",
                "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop"),
            prod(22,"Wrap Vegano","Tortilla integral con hummus y vegetales.",14000,15,"ACTIVO",
                "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&h=300&fit=crop"),
            prod(23,"Cheesecake Frutos Rojos","Cheesecake horneado con mermelada artesanal.",9000,12,"ACTIVO",
                "https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=400&h=300&fit=crop"),
            prod(24,"Granola Artesanal 300g","Avena tostada con miel y arandanos.",16000,30,"ACTIVO",
                "https://images.unsplash.com/photo-1686182689848-283fdd34e72f?w=400&h=300&fit=crop")
        ));
        list.add(fl);

        // Manos Creativas (usuario 5: Sofia)
        EmprendimientoDocument mc = emp(6,"Manos Creativas","Artesanias y productos hechos a mano.",
            "Arte",5,5,"https://images.unsplash.com/photo-1452860606245-08befc0ff44b?w=300&h=300&fit=crop",false,now);
        mc.setProductos(List.of(
            prod(26,"Taza Ceramica Artesanal","Taza moldeada y pintada a mano 350ml.",35000,8,"ACTIVO",
                "https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?w=400&h=300&fit=crop"),
            prod(27,"Maceta Colgante Tejida","Maceta en macrame con cuerda de yute.",28000,12,"ACTIVO",
                "https://images.unsplash.com/photo-1633594308237-3dcfa56b4e69?w=400&h=300&fit=crop"),
            prod(28,"Vela Aromatica Soya","Vela de soya con lavanda y eucalipto.",22000,20,"ACTIVO",
                "https://images.unsplash.com/photo-1602607203588-d6d0eda790e3?w=400&h=300&fit=crop")
        ));
        list.add(mc);

        // CodeByte (usuario 6: Miguel)
        EmprendimientoDocument cb = emp(7,"CodeByte Solutions","Servicios de desarrollo web y soporte tecnico.",
            "Servicios",6,6,"https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=300&h=300&fit=crop",false,now);
        cb.setProductos(List.of(
            prod(30,"Pagina Web Basica","Landing page con hasta 5 secciones.",250000,5,"ACTIVO",
                "https://images.unsplash.com/photo-1547658719-da2b51169166?w=400&h=300&fit=crop"),
            prod(31,"Tutoria Programacion 2h","Sesion personalizada Python, Java o JS.",40000,20,"ACTIVO",
                "https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=400&h=300&fit=crop"),
            prod(33,"Edicion Video Corto","Edicion de video hasta 3 minutos.",55000,8,"ACTIVO",
                "https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=400&h=300&fit=crop")
        ));
        list.add(cb);

        // GreenFit (usuario 2: Carlos)
        EmprendimientoDocument gf = emp(8,"GreenFit","Suplementos naturales y snacks fitness.",
            "Comida",3,2,"https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=300&h=300&fit=crop",false,now);
        gf.setProductos(List.of(
            prod(34,"Proteina Vegetal Vainilla 500g","Proteina de guisante sin lactosa.",85000,15,"ACTIVO",
                "https://images.unsplash.com/photo-1593095948071-474c5cc2989d?w=400&h=300&fit=crop"),
            prod(35,"Pack Snacks Saludables x5","Seleccion de 5 snacks variados.",32000,22,"ACTIVO",
                "https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=400&h=300&fit=crop"),
            prod(36,"Colageno en Polvo 200g","Colageno marino hidrolizado sin sabor.",65000,10,"ACTIVO",
                "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400&h=300&fit=crop")
        ));
        list.add(gf);

        // Set real emprendedor names from Supabase users (synced to MongoDB)
        list.forEach(e -> e.setEmprendedor(getEmprendedor(e.getEmprendedor().getIdUsuarioSql())));

        return list;
    }

    private EmprendimientoDocument emp(int id, String nombre, String desc, String catNombre,
                                        int catId, int usuarioId, String logo, boolean destacado, Date now) {
        EmprendimientoDocument e = new EmprendimientoDocument();
        e.setIdEmprendimientoSql(id);
        e.setNombre(nombre);
        e.setNombreEmprendimiento(nombre);
        e.setDescripcion(desc);
        e.setLogoUrl(logo);
        e.setEstado("ACTIVO");
        e.setDestacado(destacado);
        // placeholder - will be replaced with real data below
        e.setEmprendedor(new EmprendimientoDocument.EmprendedorEmbed(usuarioId, "Usuario " + usuarioId, ""));
        e.setCategoria(new EmprendimientoDocument.CategoriaEmbed(catId, catNombre));
        e.setSemestre(new EmprendimientoDocument.SemestreEmbed(1, "2026-1"));
        e.setMetricas(new EmprendimientoDocument.MetricasEmbed(0, BigDecimal.ZERO, 0, 0, 0.0, new ArrayList<>(), new ArrayList<>()));
        e.setUltimasCalificaciones(new ArrayList<>());
        e.setCreatedAt(now);
        e.setUpdatedAt(now);
        return e;
    }

    private EmprendimientoDocument.ProductoEmbed prod(int id, String nombre, String desc,
                                                       double precio, int stock, String estado, String img) {
        EmprendimientoDocument.ProductoEmbed p = new EmprendimientoDocument.ProductoEmbed();
        p.setIdProductoSql(id);
        p.setNombre(nombre);
        p.setDescripcion(desc);
        p.setPrecio(BigDecimal.valueOf(precio));
        p.setStockDisponible(stock);
        p.setEstado(estado);
        p.setImagenes(img != null ? List.of(img) : new ArrayList<>());
        p.setCreatedAt(new Date());
        p.setUpdatedAt(new Date());
        return p;
    }

    private EmprendimientoDocument.EmprendedorEmbed getEmprendedor(int idUsuario) {
        return usuarioSyncRepo.findByIdUsuarioSql(idUsuario)
            .map(u -> new EmprendimientoDocument.EmprendedorEmbed(
                u.getIdUsuarioSql(), u.getNombreCompleto(), u.getCorreoInstitucional()))
            .orElse(new EmprendimientoDocument.EmprendedorEmbed(idUsuario, "Usuario " + idUsuario, ""));
    }
}
