// Módulos NodeJS
var wDep = {
	gulp: require('gulp'), // Automatizador de tarefas
	fs: require('fs'), // Operador de sistemas de arquivos
	sass: require('gulp-sass'), //Compilador de SCSS para CSS
	sourcemaps: require('gulp-sourcemaps'), // Mapeia o código do arquivo comprimido, exibindo sua posição original
	autoprefixer: require('gulp-autoprefixer'), // Adiciona prefixos CSS magicamente
	browserSync: require('browser-sync').create(), // Recarrega o browser
	concat: require('gulp-concat'), // Concatena arquivos (Ex.: .css, .js)
	rename: require('gulp-rename'), // Renomeia arquivo (Ex.: style.css -> style.min.css)
	uglify: require('gulp-uglify'), // Minifica arquivos .js
	imagemin: require('gulp-imagemin'), // Otimiza imagens
	path: require('path'), //Diretório onde o node está sendo executado
	mmq: require('gulp-merge-media-queries'), // Mescla as definições de mdeia queries
	minifyCss: require('gulp-uglifycss') // Minifica arquivos. css
}


// Diretórios base do projeto
var wBases = {
	dev: 'dev/',
	dist: 'resources/META-INF/',
	root: './'
}

// Caminhos dos arquivos de desenvolvimento
var wPaths = {
	html: ['**/*.xhtml'],
	js: '/**/*.js',
	scss: 'dbsfaces.scss', //'*.scss',
	all: '**/*'//,
	//serverProject: 'localhost:8080/front-test/'
}

// Pastas de assets do projeto
var wFolders = {
	scss : 'scss',
	js: 'js',
	img: 'images',
	css: 'css',
	resources: 'resources/'
}

// Atributos de configuração do gulp-sass
var wSassAttrs = {
	errLogToConsole: true, 
	outputStyle: 'compact',
	precision: 10
}

// Atributos de configuração do gulp-autoprefixer
var wAutoPrefAttrs = {
	browsers: ['last 2 versions', '> 5%', 'Firefox ESR']
}
// Nome de saída dos arquivos para produção
var wOutputFiles = {
	jsMin: 'dbsfaces.min.js',
	cssMin: 'dbsfaces.min.css' //'dbsfaces.min.css'
}

// Atributos de configuração do gulp-imagemin
var wImageMinAttr = {
	gif: { interlaced: true },
	jpg: { progressive: true },
	png: { optimizationLevel: 5 }
}

// Caminhos de configuração
var wConfig = {
	scssFiles: wBases.root + wBases.dev + wFolders.scss + '/' + wPaths.scss,
	cssFolder: wBases.root + wBases.dist + wFolders.resources + wFolders.css + '/',

	jsFolder: wBases.root + wBases.dev + wFolders.js + '/',
	jsFiles: wBases.root + wBases.dev + wFolders.js + '/' + wPaths.js,
	jsDest: wBases.root + wBases.dist + wFolders.resources + wFolders.js + '/',
	
	imagesFiles: wBases.root + wBases.dev + wFolders.img + '/' + wPaths.all,
	imagesDistFolder: wBases.root + wBases.dist + wFolders.resources + wFolders.img,
	
	componentesFolder: wBases.root + wBases.dev + 'component',
}

// Função para recupera a lista de arquivos por pasta a partir de um diretório especifico
/*function getFolders(pDir) {
    return wDep.fs.readdirSync(pDir)
      .filter(function(pFile) {
        return wDep.fs.statSync(wDep.path.join(pDir, pFile)).isDirectory();
      });
}*/

/* 
 * Concatena arquivos js a partir de uma lista de pastas, concatena estes arquivos,
 * nomeia o resultado com o nome da pasta pai e copia para a pasta de destino definida
 */
/*wDep.gulp.task('join', function() {
	var xFolders = getFolders(wConfig.componentesFolder);
	var xTasks = xFolders.map(function(pFolder) {
	   return wDep.gulp.src(wDep.path.join(wConfig.componentesFolder, pFolder, wPaths.js))
		 .pipe(wDep.concat(pFolder + '.js'))
		 .pipe(wDep.gulp.dest(wConfig.jsFolder))    
	});
});*/

/* 
 * Compila SCSS para CSS, mapeia, prefixa os atributos, concatena as media querias, 
 * minifica o CSS, o renomeia com o final ".min.js", ao final move para a pasta de destino 
 * e atualiza o browser.
 */
wDep.gulp.task('sass-dev', function () {
	return wDep.gulp.src(wConfig.scssFiles)
		.pipe(wDep.sourcemaps.init())
		.pipe(wDep.sass(wSassAttrs))
		//.pipe(wDep.autoprefixer(wAutoPrefAttrs))
		//.pipe(wDep.mmq({log: true}))
		.pipe(wDep.concat(wOutputFiles.cssMin))
		//.pipe(wDep.minifyCss())
		.pipe(wDep.sourcemaps.write())
		.pipe(wDep.rename(wOutputFiles.cssMin))
		.pipe(wDep.gulp.dest(wConfig.cssFolder))
		.pipe(wDep.browserSync.stream());
});

/* 
 * Prepara o CSS para produção realizando o mesmo processo da tarefa "sass-dev", 
 * retirando as ações para mapear e atualizar o navegador.
 */
wDep.gulp.task('sass-prod', function () {
	return wDep.gulp.src(wConfig.scssFiles)
		.pipe(wDep.sass(wSassAttrs))
		.pipe(wDep.autoprefixer(wAutoPrefAttrs))
		.pipe(wDep.mmq({log: true}))
		.pipe(wDep.concat(wOutputFiles.cssMin))
		.pipe(wDep.minifyCss())
		.pipe(wDep.rename(wOutputFiles.cssMin))
		.pipe(wDep.gulp.dest(wConfig.cssFolder));
});


/* 
 * Recupera todos os arquivos JS do projeto, mapeia, concatena, minifica e copia para o destino 
 * e atualzia o browser.
 */
wDep.gulp.task('scripts-dev', function() {
	return wDep.gulp.src(wConfig.jsFiles)
		.pipe(wDep.sourcemaps.init())
        .pipe(wDep.concat(wOutputFiles.jsMin))
		.pipe(wDep.gulp.dest(wConfig.jsDest))
		.pipe(wDep.uglify())
		.pipe(wDep.sourcemaps.write())
		.pipe(wDep.gulp.dest(wConfig.jsDest));
		//.pipe(wDep.browserSync.stream());
});

/* 
 * Prepara o JS para produção, realizando o mesmo processo da tarefa "scripts-dev" sem mapear
 * e atualizar o navegador
 */
wDep.gulp.task('scripts-prod', function() {
	return wDep.gulp.src(wConfig.jsFolder + 'components/' + wPaths.js)
        .pipe(wDep.concat(wOutputFiles.jsMin))
		.pipe(wDep.gulp.dest(wConfig.jsDest))
		.pipe(wDep.uglify())
        .pipe(wDep.gulp.dest(wConfig.jsDest))
});


// Otimiza as imagens para produção
wDep.gulp.task('images', () =>
	wDep.gulp.src(wConfig.imagesFiles)
		.pipe(wDep.imagemin(
			[
				wDep.imagemin.gifsicle(wImageMinAttr.gif),
				wDep.imagemin.jpegtran(wImageMinAttr.jpg),
				wDep.imagemin.optipng(wImageMinAttr.png)
			], { verbose: true }
		))
		.pipe(wDep.gulp.dest(wConfig.imagesDistFolder))
);

// Copia os arquivos... 
wDep.gulp.task('copy',function() {
	//...js
	wDep.gulp.src([wFolders.js + '/*.js'], {cwd: wBases.dev})
		.pipe(wDep.gulp.dest(wConfig.jsDest));
});


// Observa alterações nos arquivos e atualiza o browser
wDep.gulp.task('watch', function() {
//	wDep.browserSync.init({
//		proxy: wPaths.serverProject,
//		files: wBases.root + wBases.dist + wPaths.all
//	});

	wDep.gulp.watch( wBases.dev + wPaths.all, ['sass-dev', 'copy', 'scripts-dev'])
		.on('change', function(pEvent){
			console.log('File' + pEvent.path + ' was ' + pEvent.type + ', running tasks...')
		});
});

// No terminal na pasta 'main' do projeto execute o comando 'gulp dev'
wDep.gulp.task('dev', ['sass-dev', 'copy', 'scripts-dev', 'watch']);
// No terminal na pasta 'main' do projeto execute o comando 'gulp prod'
wDep.gulp.task('prod', ['sass-prod', 'copy', 'scripts-prod', 'images']);
// No terminal na pasta 'main' do projeto execute o comando 'gulp prod-review'
wDep.gulp.task('prod-review', ['prod', 'watch']);