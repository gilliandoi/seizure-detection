library("R.matlab")
library("bmp")

#�d�ʐ�
channels <- 0

#���ԑ�
timeTotal <- 0

#�����V���[�Y
series <- 400

#��������(<399)
splitLimit <- 0

#�o�͉摜��
outWidth <- 183

#�o�͉摜��
outHeight <- 183

#���̓t�H���_�p�X�imat�j
#inputDir <- '/R/data'
inputDir <- '/R/data'

#�o�̓t�H���_�p�X(jpg)
outputDir <- '/R/output'

#�t�H���_�ő��݂���mat�t�@�C���𕪊����ďo��
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

#matLab�t�@�C���������ďo��
readMatFile <- function(path){
	setwd(inputDir)
	
	x <- readMat(path)
	
	#cat("-----------Orgin Data----------------------------------------\n")
	

	#mat�t�@�C���\���o��
	#print(x[[1]])

	# data �s��
	data <- x[[1]]
	channels = nrow(data)
	timeTotal = ncol(data)
	#cat(paste("row*col:",channels,"*",timeTotal,"\n\n"));
	
	setwd(outputDir)

		#�O���t�o�͗p�s��쐬
		outputData <- list()
		
		#�����g���g�`�F16 channel�iX�F���ԁAY�F�d�ʁj�F
		#�d�ʐ��ʐݒ�
		par(mfrow=c(16,1)) 
		#�]���̈�폜
		par(mar = c(0, 0, 0, 0))
		par(oma = c(0, 0, 0, 0)) 
		plot(0:(length(fft(data[1,]))-1),fft(data[1,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[2,]))-1),fft(data[2,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[3,]))-1),fft(data[3,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[4,]))-1),fft(data[4,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[5,]))-1),fft(data[5,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[6,]))-1),fft(data[6,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[7,]))-1),fft(data[7,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[8,]))-1),fft(data[8,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[9,]))-1),fft(data[9,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[10,]))-1),fft(data[10,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[11,]))-1),fft(data[11,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[12,]))-1),fft(data[12,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[13,]))-1),fft(data[13,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[14,]))-1),fft(data[14,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[15,]))-1),fft(data[15,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[16,]))-1),fft(data[16,]),type="l",xlab="",ylab="",axes=FALSE)
		#�o�̓t�@�C����
		outputFile <- paste(substr(path, 1, nchar(path) - 4),".bmp",sep="")
		dev.copy(bmp,outputFile,width=outWidth,height=outHeight)
		dev.off()
		
		#�e�t�@�C���o��
		cat(paste("File",outputFile," OK!\n"))
	
		#���W�o��
		#cat("-----------Output Data-------------\n")
		#str(outputData)
}

#TEST�p�FBMP�t�@�C���Ǎ���
readBMP <- function(){	
	testimg <- read.bmp('/R/output/Dog_1_interictal_segment_0001_001.bmp')
	cat(dim(testimg))
	print(testimg)
}

readMatFolder()