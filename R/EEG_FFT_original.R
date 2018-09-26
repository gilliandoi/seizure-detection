library("R.matlab")
library("bmp")

#電位数
channels <- 0

#時間帯
timeTotal <- 0

#分割シリーズ
series <- 400

#分割制限(<399)
splitLimit <- 0

#出力画像幅
outWidth <- 183

#出力画像高
outHeight <- 183

#入力フォルダパス（mat）
#inputDir <- '/R/data'
inputDir <- '/R/data'

#出力フォルダパス(jpg)
outputDir <- '/R/output'

#フォルダで存在するmatファイルを分割して出力
readMatFolder <- function(){
	setwd(inputDir)

	matfile <- dir()
	cnt <- grep("mat",matfile)
	
	#cat("-----------Split Data----------------------------------------\n")
	for ( i in 1:length(cnt)){
		path = matfile[cnt[i]];	
		print(path)
		readMatFile(path)		
	}

	cat(paste("---------------------------------------------------------------\n"))
	if(splitLimit != 0){
		cat(paste("Exported",splitLimit*length(cnt),"files!\n"))
	}
	setwd('/R')
	#source('/R/test01.R')
}

#matLabファイル分割して出力
readMatFile <- function(path){
	setwd(inputDir)
	
	x <- readMat(path)
	
	#cat("-----------Orgin Data----------------------------------------\n")
	

	#matファイル構成出力
	#print(x[[1]])

	# data 行列
	data <- x[[1]]
	channels = nrow(data)
	timeTotal = ncol(data)
	#cat(paste("row*col:",channels,"*",timeTotal,"\n\n"));
	
	setwd(outputDir)

		#グラフ出力用行列作成
		outputData <- list()
		
		#周波数（X：時間、Y：周波数）：
		#●源周波数波形（X：時間、Y：周波数）：
		par(mfrow=c(1,1)) 
		#余白領域削除
		par(oma = c(0, 0, 0, 0)) 
		par(oma = c(0, 0, 0, 0)) 
		plot(0:(length(fft(data[1,]))-1),fft(data[1,]),type="l",xlab="",ylab="",axes=FALSE,col=1)
		par(new=T)
		plot(0:(length(fft(data[2,]))-1),fft(data[2,]),type="l",xlab="",ylab="",axes=FALSE,col=2)
		par(new=T)
		plot(0:(length(fft(data[3,]))-1),fft(data[3,]),type="l",xlab="",ylab="",axes=FALSE,col=3)
		par(new=T)
		plot(0:(length(fft(data[4,]))-1),fft(data[4,]),type="l",xlab="",ylab="",axes=FALSE,col=4)
		par(new=T)
		plot(0:(length(fft(data[5,]))-1),fft(data[5,]),type="l",xlab="",ylab="",axes=FALSE,col=6)
		par(new=T)
		plot(0:(length(fft(data[6,]))-1),fft(data[6,]),type="l",xlab="",ylab="",axes=FALSE,col=8)
		par(new=T)
		plot(0:(length(fft(data[7,]))-1),fft(data[7,]),type="l",xlab="",ylab="",axes=FALSE,col=9)
		par(new=T)
		plot(0:(length(fft(data[8,]))-1),fft(data[8,]),type="l",xlab="",ylab="",axes=FALSE,col=10)
		par(new=T)
		plot(0:(length(fft(data[9,]))-1),fft(data[9,]),type="l",xlab="",ylab="",axes=FALSE,col=11)
		par(new=T)
		plot(0:(length(fft(data[10,]))-1),fft(data[10,]),type="l",xlab="",ylab="",axes=FALSE,col=12)
		par(new=T)
		plot(0:(length(fft(data[11,]))-1),fft(data[11,]),type="l",xlab="",ylab="",axes=FALSE,col=13)
		par(new=T)
		plot(0:(length(fft(data[12,]))-1),fft(data[12,]),type="l",xlab="",ylab="",axes=FALSE,col=14)
		par(new=T)
		plot(0:(length(fft(data[13,]))-1),fft(data[13,]),type="l",xlab="",ylab="",axes=FALSE,col=15)
		par(new=T)
		plot(0:(length(fft(data[14,]))-1),fft(data[14,]),type="l",xlab="",ylab="",axes=FALSE,col=16)
		par(new=T)
		plot(0:(length(fft(data[15,]))-1),fft(data[15,]),type="l",xlab="",ylab="",axes=FALSE,col=17)
		par(new=T)
		plot(0:(length(fft(data[16,]))-1),fft(data[16,]),type="l",xlab="",ylab="",axes=FALSE,col=18)
		#出力ファイル名
		outputFile <- paste(substr(path, 1, nchar(path) - 4),".bmp",sep="")
		dev.copy(bmp,outputFile,width=outWidth,height=outHeight)
		dev.off()
		
		#各ファイル出力
		cat(paste("File",outputFile," OK!\n"))
	
		#座標出力
		#cat("-----------Output Data-------------\n")
		#str(outputData)
}

#TEST用：BMPファイル読込む
readBMP <- function(){	
	testimg <- read.bmp('/R/output/Dog_1_interictal_segment_0001_001.bmp')
	cat(dim(testimg))
	print(testimg)
}

readMatFolder()